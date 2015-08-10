package com.devicehive.application.websocket;

import com.devicehive.configuration.ConfigurationService;
import com.devicehive.configuration.Constants;
import com.devicehive.websockets.ClientWebSocketHandler;
import com.devicehive.websocket.DeviceWebSocketEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(deviceHandler(), "/websocket/device").setAllowedOrigins("*")
                .addHandler(clientHandler(), "/websocket/client").setAllowedOrigins("*")

                .addHandler(deviceHandlerV2(), "/websocket/device/v2").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler deviceHandler() {
        return new com.devicehive.websockets.DeviceWebSocketHandler();
    }

    @Bean
    public WebSocketHandler clientHandler() {
        return new ClientWebSocketHandler();
    }

    @Bean
    public WebSocketHandler deviceHandlerV2() {
        return new DeviceWebSocketEndpoint();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(Constants.WEBSOCKET_MAX_BUFFER_SIZE);
        container.setMaxTextMessageBufferSize(Constants.WEBSOCKET_MAX_BUFFER_SIZE);
        container.setMaxSessionIdleTimeout(
                configurationService.getLong(Constants.WEBSOCKET_SESSION_PING_TIMEOUT, Constants.WEBSOCKET_SESSION_PING_TIMEOUT_DEFAULT));
        return container;
    }

}
