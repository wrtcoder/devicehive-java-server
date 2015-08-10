package com.devicehive.websocket.handler.common;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import com.devicehive.json.domain.ApiInfo;
import com.devicehive.service.time.TimestampService;
import com.devicehive.websocket.action.ServerInfo;
import com.devicehive.websocket.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.concurrent.Future;

/**
 * Handler for "server/info" actions.
 *
 * @see ServerInfo
 * @see ApiInfo
 */
@Component
public class ServerInfoRequestHandler implements RequestHandler<ServerInfo, ApiInfo> {
    private static final Logger logger = LoggerFactory.getLogger(ServerInfoRequestHandler.class);

    @Value("${build.version}")
    private String buildVersion;
    @Autowired
    private TimestampService timestampService;
    @Autowired
    private ActorSystem system;

    @Override
    public Future<ApiInfo> handleRequest(ServerInfo action) {
        return Futures.future(() -> {
            logger.info("Requesting server/info");
            return new ApiInfo(buildVersion, timestampService.getTimestamp());
        }, system.dispatcher());
    }

}
