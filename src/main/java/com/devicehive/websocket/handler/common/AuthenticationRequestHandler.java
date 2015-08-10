package com.devicehive.websocket.handler.common;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import com.devicehive.auth.HiveAuthentication;
import com.devicehive.auth.rest.providers.DeviceAuthenticationToken;
import com.devicehive.websocket.action.AuthenticateAction;
import com.devicehive.websocket.handler.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import scala.concurrent.Future;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handler for web socket requests with "authenticate" action.
 * Accepts {@link AuthenticateAction} as a parameter and adds entry  ["authentication" -> {@link HiveAuthentication}] to web socket session attributes
 *
 * @see AuthenticateAction
 * @see HiveAuthentication
 */
@Component
public class AuthenticationRequestHandler implements RequestHandler<AuthenticateAction, Void> {

    @Autowired
    private ActorSystem system;
    @Autowired
    private AuthenticationManager authManager;

    @Override
    public Future<Void> handleRequest(AuthenticateAction action) {
        return Futures.future(() -> {
            Optional<HiveAuthentication> authentication;
            if (action.getUserAuth().isPresent()) {
                authentication = action.getUserAuth().map(user -> processUserAuth(user.getLogin(), user.getPassword()));
            } else if (action.getDeviceAuth().isPresent()) {
                authentication = action.getDeviceAuth().map(device -> processDeviceAuth(device.getDeviceId(), device.getDeviceKey()));
            } else if (action.getKey().isPresent()) {
                authentication = action.getKey().map(this::processKeyAuth);
            } else {
                authentication = Optional.of(processAnonymousAuth());
            }

            authentication.ifPresent(auth -> {
                HiveAuthentication.HiveAuthDetails details = getDetails(action.getSession());
                auth.setDetails(details);
                action.getSession().getAttributes().put("authentication", auth);
            });

            return null;
        }, system.dispatcher());
    }

    private HiveAuthentication.HiveAuthDetails getDetails(WebSocketSession session) {
        List<String> originList = session.getHandshakeHeaders().get(HttpHeaders.ORIGIN);
        List<String> authList = session.getHandshakeHeaders().get(HttpHeaders.AUTHORIZATION);
        String origin = originList == null || originList.isEmpty() ? null : originList.get(0);
        String auth = authList == null || authList.isEmpty() ? null : authList.get(0);

        return new HiveAuthentication.HiveAuthDetails(session.getRemoteAddress().getAddress(), origin, auth);
    }

    private HiveAuthentication processUserAuth(String login, String password) {
        return processAuth(new UsernamePasswordAuthenticationToken(login, password));
    }

    private HiveAuthentication processDeviceAuth(String deviceId, String deviceKey) {
        return processAuth(new DeviceAuthenticationToken(deviceId, deviceKey));
    }

    private HiveAuthentication processKeyAuth(String accessKey) {
        return processAuth(new PreAuthenticatedAuthenticationToken(accessKey, null));
    }

    public HiveAuthentication processAnonymousAuth() {
        return processAuth(
                new AnonymousAuthenticationToken(
                        UUID.randomUUID().toString(), "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
    }

    public HiveAuthentication processAuth(Authentication auth) {
        return (HiveAuthentication) authManager.authenticate(auth);
    }
}
