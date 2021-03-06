package com.devicehive.model.oauth;

import com.devicehive.configuration.Messages;
import com.devicehive.exceptions.HiveException;
import com.devicehive.service.AccessKeyService;
import com.devicehive.service.UserService;
import com.devicehive.vo.AccessKeyRequestVO;
import com.devicehive.vo.AccessKeyVO;
import com.devicehive.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

/**
 * Created by tmatvienko on 1/13/15.
 */
@Component
public class PasswordIdentityProvider extends AuthProvider {
    private static final Logger logger = LoggerFactory.getLogger(PasswordIdentityProvider.class);

    private static final String PASSWORD_PROVIDER_NAME = "Password";

    @Autowired
    private UserService userService;
    @Autowired
    private AccessKeyService accessKeyService;

    @Override
    public boolean isIdentityProviderAllowed() {
        return true;
    }

    @Override
    public AccessKeyVO createAccessKey(@NotNull final AccessKeyRequestVO request) {
        if (StringUtils.isBlank(request.getLogin()) || StringUtils.isBlank(request.getPassword())) {
            logger.error(Messages.INVALID_AUTH_REQUEST_PARAMETERS);
            throw new HiveException(Messages.INVALID_AUTH_REQUEST_PARAMETERS, Response.Status.BAD_REQUEST.getStatusCode());
        }
        final UserVO user = findUser(request.getLogin(), request.getPassword());
        return accessKeyService.authenticate(user);
    }

    private UserVO findUser(String login, String password) {
        return userService.findUser(login, password);
    }
}
