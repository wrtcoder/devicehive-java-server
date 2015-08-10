package com.devicehive.websocket.handler;

import com.devicehive.websocket.action.Action;
import scala.concurrent.Future;

/**
 * Base interface for web socket action handlers.
 *
 * @param <T> type of input action
 * @param <U> type of output
 *
 * @see Action
 */
public interface RequestHandler<T extends Action, U> {

    /**
     * Handles web socket action and returns result.
     * Result is later added to web socket response as {@link com.devicehive.json.domain.WebSocketResponse#handlerResponse}
     *
     * @param action input action if type {@link Action}
     * @return future with result of action
     */
    Future<U> handleRequest(T action);

}
