package com.devicehive.json.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;

@JsonRootName("info")
public class ApiInfo {

    @JsonProperty("apiVersion")
    private String apiVersion;

    @JsonProperty("serverTimestamp")
    private Date serverTimestamp;

    @JsonProperty("webSocketServerUrl")
    private String webSocketServerUrl;

    public ApiInfo() {
    }

    public ApiInfo(String apiVersion, Date serverTimestamp) {
        this.apiVersion = apiVersion;
        this.serverTimestamp = serverTimestamp;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public String getWebSocketServerUrl() {
        return webSocketServerUrl;
    }

    public void setWebSocketServerUrl(String webSocketServerUrl) {
        this.webSocketServerUrl = webSocketServerUrl;
    }
}
