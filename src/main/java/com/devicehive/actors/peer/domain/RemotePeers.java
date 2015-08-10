package com.devicehive.actors.peer.domain;

import org.springframework.web.socket.WebSocketSession;

import javax.ws.rs.container.AsyncResponse;

public final class RemotePeers {

    public static RemotePeer webSocketPeer(WebSocketSession session) {
        return new WebSocketRemotePeer(session);
    }

    public static LongPollingRemotePeer longPollingPeer(AsyncResponse response) {
        return new LongPollingRemotePeer(response);
    }

}
