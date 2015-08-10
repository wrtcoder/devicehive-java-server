package com.devicehive.websocket.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class AuthenticateAction extends Action {

    private Optional<UserAuth> userAuth = Optional.empty();

    private Optional<DeviceAuth> deviceAuth = Optional.empty();

    private Optional<String> key = Optional.empty();

    @JsonCreator
    public AuthenticateAction(@JsonProperty("login") String login, @JsonProperty("password") String password,
                              @JsonProperty("deviceId") String deviceId, @JsonProperty("deviceKey") String deviceKey,
                              @JsonProperty("accessKey") String key) {
        if (login != null && password != null) {
            userAuth = Optional.of(new UserAuth(login, password));
        } else if (deviceId != null && deviceKey != null) {
            deviceAuth = Optional.of(new DeviceAuth(deviceId, deviceKey));
        } else {
            this.key = Optional.ofNullable(key);
        }
    }

    public Optional<UserAuth> getUserAuth() {
        return userAuth;
    }

    public Optional<DeviceAuth> getDeviceAuth() {
        return deviceAuth;
    }

    public Optional<String> getKey() {
        return key;
    }

    public static class UserAuth {
        private String login;
        private String password ;

        public UserAuth(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class DeviceAuth {
        private String deviceId;
        private String deviceKey;

        public DeviceAuth(String deviceId, String deviceKey) {
            this.deviceId = deviceId;
            this.deviceKey = deviceKey;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getDeviceKey() {
            return deviceKey;
        }
    }

}
