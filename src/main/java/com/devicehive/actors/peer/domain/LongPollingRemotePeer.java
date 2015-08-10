package com.devicehive.actors.peer.domain;

import javax.ws.rs.container.AsyncResponse;
import java.util.Objects;
import java.util.UUID;

class LongPollingRemotePeer implements RemotePeer {

    private final String id;
    private final AsyncResponse response;

    public LongPollingRemotePeer(AsyncResponse response) {
        if (response == null)
            throw new NullPointerException("Async response should not be null");

        this.id = UUID.randomUUID().toString();
        this.response = response;
    }

    @Override
    public void sendMessage(Object msg) throws Exception {
        response.resume(response);
    }

    @Override
    public void sendPingMessage() throws Exception {
        //no-op for async response
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isOpen() {
        return !(response.isCancelled() || response.isDone());
    }

    @Override
    public String toString() {
        return "longPollingRequest(id = " + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongPollingRemotePeer)) return false;
        LongPollingRemotePeer that = (LongPollingRemotePeer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
