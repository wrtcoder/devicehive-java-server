package com.devicehive.actors.peer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.testkit.JavaTestKit;
import akka.util.Timeout;
import com.devicehive.actors.messages.Done;
import com.devicehive.actors.messages.SendMessage;
import com.devicehive.actors.messages.SendPingMessage;
import com.devicehive.actors.peer.domain.RemotePeer;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.application.akka.SpringExtension;
import com.devicehive.base.AbstractResourceTest;
import com.devicehive.base.fixture.RemotePeerFixture;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

public class RemotePeerActorTest extends AbstractResourceTest {

    @Autowired
    private ActorSystem system;
    @Autowired
    private SpringExtension springExt;
    @Autowired
    private Timeout timeout;


    @Test
    public void should_send_message_to_open_peer() throws Exception {
        new JavaTestKit(system) {{
            RemotePeer peer = RemotePeerFixture.createPeer(RandomStringUtils.randomAlphabetic(10), true);
            final ActorRef actor = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer), "peer-ws-" + peer.getId());

            actor.tell(new SendMessage("some message"), getRef());
            expectMsgClass(timeout.duration(), Done.class);
            Mockito.verify(peer).isOpen();
            Mockito.verify(peer).sendMessage("some message");
        }};
    }

    @Test
    public void should_stop_actor_if_peer_is_closed() throws Exception {
        new JavaTestKit(system) {{
            RemotePeer peer = RemotePeerFixture.createPeer(RandomStringUtils.randomAlphabetic(10), false);
            final ActorRef actor = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer), "peer-ws-" + peer.getId());

            watch(actor);

            actor.tell(new SendMessage("some message"), getRef());
            expectMsgClass(timeout.duration(), Terminated.class);
            Mockito.verify(peer).isOpen();
            Mockito.verify(peer, times(0)).sendMessage(anyString());
        }};
    }

    @Test
    public void should_send_ping_message_to_open_peer() throws Exception {
        new JavaTestKit(system) {{
            RemotePeer peer = RemotePeerFixture.createPeer(RandomStringUtils.randomAlphabetic(10), true);
            final ActorRef actor = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer), "peer-ws-" + peer.getId());

            actor.tell(new SendPingMessage(), getRef());
            expectMsgClass(timeout.duration(), Done.class);
            Mockito.verify(peer).isOpen();
            Mockito.verify(peer).sendPingMessage();
        }};
    }

    @Test
    public void should_stop_actor_during_ping_if_peer_is_closed() throws Exception {
        new JavaTestKit(system) {{
            RemotePeer peer = RemotePeerFixture.createPeer(RandomStringUtils.randomAlphabetic(10), false);
            final ActorRef actor = system.actorOf(springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer), "peer-ws-" + peer.getId());

            watch(actor);

            actor.tell(new SendPingMessage(), getRef());
            expectMsgClass(timeout.duration(), Terminated.class);
            Mockito.verify(peer).isOpen();
            Mockito.verify(peer, times(0)).sendPingMessage();
        }};
    }
}
