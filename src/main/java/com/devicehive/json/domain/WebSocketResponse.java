package com.devicehive.json.domain;

import com.devicehive.json.serializer.WebSocketResponseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Json web socket response.
 *
 * Serialization is handled by custom serializer {@link com.devicehive.json.serializer.WebSocketResponseSerializer}
 */
@JsonSerialize(using = WebSocketResponseSerializer.class)
public class WebSocketResponse {

    private Optional<String> action;
    private Optional<String> requestId;
    private String status;
    private Optional<ErrorDescription> error = Optional.empty();

    private Object handlerResponse;

    public Optional<String> getAction() {
        return action;
    }

    public Optional<String> getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public Optional<ErrorDescription> getError() {
        return error;
    }

    public Object getHandlerResponse() {
        return handlerResponse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String action;
        private Optional<String> requestId;
        private String status;
        private ErrorDescription err;
        private Object handlerResponse;

        public Builder withAction(String action) {
            this.action = action;
            return this;
        }

        public Builder withRequestId(Optional<String> requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withResponse(Object response) {
            this.handlerResponse = response;
            return this;
        }

        public Builder withError(ErrorDescription err) {
            this.err = err;
            return this;
        }

        public WebSocketResponse build() {
            Assert.hasText(status);

            WebSocketResponse response = new WebSocketResponse();
            response.action = Optional.ofNullable(action);
            response.requestId = requestId;
            response.status = status;
            response.handlerResponse = handlerResponse;
            response.error = Optional.ofNullable(err);
            return response;
        }
    }
}
