package com.devicehive.actors.peer.domain;

import com.devicehive.configuration.Constants;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

class WebSocketRemotePeer implements RemotePeer {

    private final WebSocketSession session;

    public WebSocketRemotePeer(WebSocketSession session) {
        if (session == null)
            throw new NullPointerException("Web Socket session should not be null");
        this.session = session;
    }

    @Override
    public void sendMessage(Object msg) throws Exception {
        session.sendMessage(new TextMessage(msg.toString()));
    }

    @Override
    public void sendPingMessage() throws Exception {
        session.sendMessage(new PingMessage(Constants.PING));
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public String toString() {
        return "websocket(id="+ session.getId() +")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSocketRemotePeer)) return false;
        WebSocketRemotePeer that = (WebSocketRemotePeer) o;
        return Objects.equals(session.getId(), that.session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(session.getId());
    }
}
