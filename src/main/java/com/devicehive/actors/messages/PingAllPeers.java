package com.devicehive.actors.messages;

/**
 * Used as a message for actors
 *
 * @see com.devicehive.actors.peer.RemotePeersPingActor
 */
public class PingAllPeers {
    private static PingAllPeers instance = new PingAllPeers();

    private PingAllPeers() { }

    public static PingAllPeers getInstance() {
        return instance;
    }
}
