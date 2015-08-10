package com.devicehive.websocket.domain;

import com.devicehive.websocket.action.Action;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

public class WebSocketRequest {

    private Action action;

    private WebSocketSession session;

    public WebSocketRequest(Action action, WebSocketSession session) {
        this.action = action;
        this.session = session;
    }

    public Action getAction() {
        return action;
    }

    public WebSocketSession getSession() {
        return session;
    }

}
