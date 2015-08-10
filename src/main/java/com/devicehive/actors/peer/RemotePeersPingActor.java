package com.devicehive.actors.peer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.AllForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.BroadcastGroup;
import com.devicehive.actors.messages.PingAllPeers;
import com.devicehive.actors.messages.SendPingMessage;
import com.devicehive.application.akka.ActorsConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component(ActorsConfig.ActorBeanName.PING_ACTOR)
@Scope("prototype")
public class RemotePeersPingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public RemotePeersPingActor() {
        receive(ReceiveBuilder
                        .match(PingAllPeers.class, msg -> {
                            List<String> paths = Collections.singletonList("/user/peer-ws-*");
                            ActorRef router = context().actorOf(new BroadcastGroup(paths).props());
                            router.forward(new SendPingMessage(), context());
                        })
                        .matchAny(msg -> {
                            log.warning("Unhandled message {}", msg.getClass());
                            unhandled(msg);
                        }).build()
        );
    }

}
