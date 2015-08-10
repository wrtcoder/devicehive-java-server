package com.devicehive.websocket;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.util.Timeout;
import com.devicehive.actors.messages.SendMessage;
import com.devicehive.actors.peer.domain.RemotePeer;
import com.devicehive.actors.peer.domain.RemotePeers;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.application.akka.SpringExtension;
import com.devicehive.json.domain.ErrorDescription;
import com.devicehive.json.domain.WebSocketResponse;
import com.devicehive.websocket.action.Action;
import com.devicehive.websocket.domain.WebSocketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import scala.concurrent.Future;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static akka.dispatch.Futures.future;
import static akka.pattern.Patterns.ask;
import static com.devicehive.application.akka.ActorsConfig.PEER_NAME_PATTERN;

public class DeviceWebSocketEndpoint extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(DeviceWebSocketEndpoint.class);

    public static final int WEBSOCKET_MAX_BUFFER_SIZE = 10 * 1024;

    @Autowired
    private ActorSystem system;
    @Autowired
    private SpringExtension springExt;
    @Autowired
    private Timeout timeout;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setBinaryMessageSizeLimit(WEBSOCKET_MAX_BUFFER_SIZE);
        session.setTextMessageSizeLimit(WEBSOCKET_MAX_BUFFER_SIZE);
        logger.info("Web Socket connection {} established", session.getId());

        final RemotePeer peer = RemotePeers.webSocketPeer(session);
        final Props peerProps = springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer);
        final String peerName = String.format(PEER_NAME_PATTERN, session.getId());
        final ActorRef remotePeerActor = system.actorOf(peerProps, peerName);
        logger.debug("Remote peer for session {} created, path = {}", session.getId(), remotePeerActor.path());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.debug("Ping response {}", session.getId());
        //TODO update device
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ActorRef dispatcher = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.WEB_SOCKET_REQUEST_DISPATCHER));

        future(() -> mapper.readValue(message.getPayload(), Action.class), system.dispatcher())
                .flatMap(new Mapper<Action, Future<Object>>() {
                    @Override
                    public Future<Object> apply(Action action) {
                        action.getRequestId()
                                .ifPresent(reqId -> session.getAttributes().put("requestId", reqId));
                        action.setSession(session);
                        return ask(dispatcher, action, timeout);
                    }
                }, system.dispatcher())
                .onComplete(new OnComplete<Object>() {
                    @Override
                    public void onComplete(Throwable failure, Object success) throws Throwable {
                        if (failure != null) {
                            handleTransportError(session, failure);
                        }
                        session.getAttributes().remove("requestId");
                        logger.debug("Stopping request dispatcher {}", dispatcher.path());
                        system.stop(dispatcher);
                    }
                }, system.dispatcher());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.warn("Connection {} closed with status {}", session.getId(), status);

        String actorName = String.format(PEER_NAME_PATTERN, session.getId());

        system.actorSelection("/user/" + actorName).resolveOne(timeout).onComplete(new OnComplete<ActorRef>() {
            @Override
            public void onComplete(Throwable failure, ActorRef remotePeer) throws Throwable {
                if (failure == null) {
                    system.stop(remotePeer);
                    logger.info("Remote peer {} stopped", remotePeer.path());
                } else {
                    logger.error("Error trying to find remote peer actor", failure);
                }
            }
        }, system.dispatcher());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Request error for session " + session.getId(), exception);

        ErrorDescription err;
        if (exception instanceof JsonParseException) {
            err = new ErrorDescription(HttpServletResponse.SC_BAD_REQUEST, "Incorrect JSON syntax");
        } else if (exception instanceof AuthenticationException) {
            err = new ErrorDescription(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        } else {
            err = new ErrorDescription(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        WebSocketResponse response = WebSocketResponse.builder()
                .withRequestId(Optional.ofNullable(session.getAttributes().get("requestId")).map(Object::toString))
                .withStatus("error")
                .withError(err)
                .build();

        String actorName = String.format(PEER_NAME_PATTERN, session.getId());
        ask(system.actorSelection("/user/" + actorName),
                new SendMessage(mapper.writeValueAsString(response)),
                timeout);
    }

}
