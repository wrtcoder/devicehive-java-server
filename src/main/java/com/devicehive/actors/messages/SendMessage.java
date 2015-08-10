package com.devicehive.actors.messages;

import java.util.Objects;

public class SendMessage {
    private final Object message;

    public SendMessage(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SendMessage)) return false;
        SendMessage that = (SendMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

}
