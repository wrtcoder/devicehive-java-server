package com.devicehive.websocket;

import akka.actor.*;
import akka.dispatch.Mapper;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.devicehive.actors.messages.SendMessage;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.application.akka.SpringExtension;
import com.devicehive.json.domain.WebSocketResponse;
import com.devicehive.websocket.action.Action;
import com.devicehive.websocket.handler.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.stop;

@Component(ActorsConfig.ActorBeanName.WEB_SOCKET_REQUEST_DISPATCHER)
@Scope("prototype")
public class WebSocketRequestDispatcher extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private SpringExtension springExt;

    private ActorRef sender;
    private ActorRef remotePeer;
    private ActorRef requestHandlerMapper;

    private Action action;

    public WebSocketRequestDispatcher() {
        receive(ReceiveBuilder
                        .match(Action.class, action -> {
                            this.sender = sender();
                            this.action = action;

                            String peerActorPath = "/user/" + String.format(ActorsConfig.PEER_NAME_PATTERN, action.getSession().getId());
                            context().actorSelection(peerActorPath).tell(new Identify("peer"), self());
                        })
                        .match(ActorIdentity.class, id -> id.getRef() != null, id -> {
                            remotePeer = id.getRef();
                            requestHandlerMapper.tell(action, self());
                        })
                        .match(RequestHandler.class, this::askHandler)
                        .matchAny(msg -> {
                            log.warning("Unhandled message {}", msg.getClass());
                            unhandled(msg);
                        })
                        .build()
        );
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(0, Duration.Zero(), failure -> {
            sender.tell(new Status.Failure(failure), self());
            return stop();
        });
    }

    @Override
    public void preStart() throws Exception {
        if (requestHandlerMapper == null) {
            requestHandlerMapper = context().actorOf(springExt.props(ActorsConfig.ActorBeanName.REQUEST_HANDLER_MAPPER));
        }
    }

    private void askHandler(RequestHandler requestHandler) {
        @SuppressWarnings("unchecked")
        Future<String> futureResult = requestHandler.handleRequest(action).map(new Mapper<Object, String>() {
            @Override
            public String apply(Object handlerResponse) {
                WebSocketResponse response = WebSocketResponse.builder()
                        .withAction(action.getAction())
                        .withRequestId(action.getRequestId())
                        .withStatus("success")
                        .withResponse(handlerResponse)
                        .build();
                return serializeResponse(response);
            }
        }, context().dispatcher());

        futureResult.onSuccess(new OnSuccess<String>() {
            @Override
            public void onSuccess(String result) throws Throwable {
                remotePeer.tell(new SendMessage(result), sender);
            }
        }, context().dispatcher());

        futureResult.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                sender.tell(new Status.Failure(failure), self());
            }
        }, context().dispatcher());
    }

    private String serializeResponse(WebSocketResponse response) {
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
