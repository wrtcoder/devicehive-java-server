package com.devicehive.websocket.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "action",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ServerInfo.class, name = "server/info"),
        @JsonSubTypes.Type(value = Echo.class, name = "echo"),
        @JsonSubTypes.Type(value = AuthenticateAction.class, name = "authenticate")
})
public abstract class Action {

    @JsonProperty("action")
    private String action;

    @JsonProperty("requestId")
    private Optional<String> requestId = Optional.empty();

    @JsonIgnore
    private WebSocketSession session;

    public String getAction() {
        return action;
    }

    public Optional<String> getRequestId() {
        return requestId;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}
