package com.devicehive.websocket;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.devicehive.actors.EscalatingActor;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.websocket.action.Action;
import com.devicehive.websocket.action.AuthenticateAction;
import com.devicehive.websocket.action.Echo;
import com.devicehive.websocket.action.ServerInfo;
import com.devicehive.websocket.handler.common.AuthenticationRequestHandler;
import com.devicehive.websocket.handler.common.EchoRequestHandler;
import com.devicehive.websocket.handler.RequestHandler;
import com.devicehive.websocket.handler.common.ServerInfoRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(ActorsConfig.ActorBeanName.REQUEST_HANDLER_MAPPER)
@Scope("prototype")
public class RequestHandlerMapper extends EscalatingActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Autowired
    private ApplicationContext springContext;

    public RequestHandlerMapper() {
        receive(ReceiveBuilder
                        .match(Echo.class, action -> findAndRespond(EchoRequestHandler.class))
                        .match(ServerInfo.class, action -> findAndRespond(ServerInfoRequestHandler.class))
                        .match(AuthenticateAction.class, action -> findAndRespond(AuthenticationRequestHandler.class))
                        .match(Action.class, action -> {
                            log.error("Unknown instance of action {}", action.getClass());
                            throw new IllegalStateException("Handler not found");
                        })
                        .matchAny(msg -> {
                            log.warning("Unhandled message {}", msg);
                            unhandled(msg);
                        })
                        .build()
        );
    }

    private void findAndRespond(Class<? extends RequestHandler> cl) {
        sender().tell(springContext.getBean(cl), self());
    }
}
