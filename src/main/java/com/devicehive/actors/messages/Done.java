package com.devicehive.actors.messages;

public class Done {
    private static final Done instance = new Done();

    private Done() {}

    public static Done getInstance() {
        return instance;
    }

}
