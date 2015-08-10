package com.devicehive.actors.peer.domain;

public interface RemotePeer {

    void sendMessage(Object msg) throws Exception;

    void sendPingMessage() throws Exception;

    String getId();

    boolean isOpen();

}
