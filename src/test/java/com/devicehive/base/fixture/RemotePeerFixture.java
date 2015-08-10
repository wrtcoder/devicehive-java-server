package com.devicehive.base.fixture;

import com.devicehive.actors.peer.domain.RemotePeer;
import org.mockito.Mockito;

public class RemotePeerFixture {

    public static RemotePeer createPeer(String id, boolean isOpen) {
        return Mockito.spy(new TestRemotePeer(id, isOpen));
    }

    public static class TestRemotePeer implements RemotePeer {

        private String id;
        private boolean open;

        public TestRemotePeer(String id, boolean open) {
            this.id = id;
            this.open = open;
        }

        @Override
        public void sendMessage(Object msg) throws Exception {
            //no op
        }

        @Override
        public void sendPingMessage() throws Exception {
            //no op
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isOpen() {
            return open;
        }
    }
}
