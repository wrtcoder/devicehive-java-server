package com.devicehive.actors.peer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.util.Timeout;
import com.devicehive.actors.messages.Done;
import com.devicehive.actors.messages.PingAllPeers;
import com.devicehive.actors.peer.domain.RemotePeer;
import com.devicehive.application.akka.ActorsConfig;
import com.devicehive.application.akka.SpringExtension;
import com.devicehive.base.AbstractResourceTest;
import com.devicehive.base.fixture.RemotePeerFixture;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.verify;

public class RemotePeersPingActorTest extends AbstractResourceTest {

    @Autowired
    private ActorSystem system;
    @Autowired
    private SpringExtension springExt;
    @Autowired
    private Timeout timeout;

    @Test
    public void should_ping_actors() throws Exception {
        new JavaTestKit(system) {{
            List<RemotePeer> peers = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                RemotePeer peer = RemotePeerFixture.createPeer(RandomStringUtils.randomAlphabetic(10), true);
                TestActorRef actor = TestActorRef.create(system, springExt.props(ActorsConfig.ActorBeanName.REMOTE_PEER_ACTOR, peer), "peer-ws-" + peer.getId());
                peers.add(peer);
            }
            ActorRef pingActor = TestActorRef.create(system, springExt.props(ActorsConfig.ActorBeanName.PING_ACTOR));
            pingActor.tell(PingAllPeers.getInstance(), getRef());

            for (int i = 0; i < 10; i++) {
                expectMsgEquals(timeout.duration(), Done.getInstance());
            }

            for (RemotePeer peer : peers) {
                verify(peer).isOpen();
                verify(peer).sendPingMessage();
            }
        }};
    }

}
