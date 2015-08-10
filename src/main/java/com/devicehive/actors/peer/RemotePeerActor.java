package com.devicehive.actors.peer;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.devicehive.actors.messages.*;
import com.devicehive.actors.peer.domain.RemotePeer;
import com.devicehive.application.akka.ActorsConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Actor which is used to interact with remote peer (web socket or http).
 * Should be used per connection
 */
@Component(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR)
@Scope("prototype")
public class RemotePeerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private RemotePeer peer;

    public RemotePeerActor(RemotePeer peer) {
        this.peer = peer;
        receive(ReceiveBuilder
                        .match(SendMessage.class, msg -> handleSendMessage(msg.getMessage()))
                        .match(SendPingMessage.class, msg -> handleSendPingMessage())
                        .matchAny(msg -> {
                            log.warning("Unhandled message {}", msg.getClass());
                            unhandled(msg);
                        }).build()
        );
    }

    private void handleSendMessage(Object message) throws Exception {
        if (peer == null)
            throw new NullPointerException("Peer is not initialized");

        if (peer.isOpen()) {
            peer.sendMessage(message);

            log.info("Message to remote peer {} sent", peer);
            log.debug("Message {} to remote peer {} sent", message, peer);

            sender().tell(Done.getInstance(), self());
        } else {
            log.error("Peer {} is closed, can not send anything", peer);
            context().stop(self());
        }

    }

    private void handleSendPingMessage() throws Exception {
        if (peer.isOpen()) {
            log.debug("Sending ping message to {}", peer);
            peer.sendPingMessage();
            sender().tell(Done.getInstance(), self());
        } else {
            log.warning("Can not send ping message to closed peer");
            context().stop(self());
        }
    }
}
