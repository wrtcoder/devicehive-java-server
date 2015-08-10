package com.devicehive.websocket.handler.common;

import com.devicehive.websocket.action.Echo;
import com.devicehive.websocket.handler.RequestHandler;
import org.springframework.stereotype.Component;
import scala.concurrent.Future;

import static akka.dispatch.Futures.successful;

@Component
public class EchoRequestHandler implements RequestHandler<Echo, String> {

    @Override
    public Future<String> handleRequest(Echo action) {
        return successful("hi there");
    }

}

