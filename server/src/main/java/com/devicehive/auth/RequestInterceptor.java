package com.devicehive.auth;

import com.devicehive.configuration.Messages;
import com.devicehive.exceptions.HiveException;
import com.devicehive.model.AccessKey;
import com.devicehive.model.AccessKeyPermission;
import com.devicehive.model.Device;
import com.devicehive.model.User;
import com.devicehive.model.enums.UserStatus;
import com.devicehive.util.ThreadLocalVariablesKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Interceptor
@AllowedKeyAction
@Priority(Interceptor.Priority.APPLICATION + 300)
public class RequestInterceptor {

    private static Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);


    private HiveSecurityContext hiveSecurityContext;

    @Inject
    public void setHiveSecurityContext(HiveSecurityContext hiveSecurityContext) {
        this.hiveSecurityContext = hiveSecurityContext;
    }

    @AroundInvoke
    public Object checkPermissions(InvocationContext context) throws Exception {
        try {
            HivePrincipal principal = hiveSecurityContext.getHivePrincipal();
            User user = principal.getUser();
            if (user != null && user.getStatus() != UserStatus.ACTIVE) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            Device device = principal.getDevice();
            if (device != null && Boolean.TRUE.equals(device.getBlocked())) {
                throw new HiveException(String.format(Messages.DEVICE_IS_BLOCKED, device.getGuid()), FORBIDDEN.getStatusCode());
            }
            AccessKey key = principal.getKey();
            if (key == null) {
                return context.proceed();
            }
            if (key.getUser() == null || !key.getUser().getStatus().equals(UserStatus.ACTIVE)) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            Timestamp expirationDate = key.getExpirationDate();
            if (expirationDate != null && expirationDate.before(new Timestamp(System.currentTimeMillis()))) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            Method method = context.getMethod();
            AllowedKeyAction allowedActionAnnotation = method.getAnnotation(AllowedKeyAction.class);
            Set<AccessKeyPermission>
                filtered =
                CheckPermissionsHelper.filterPermissions(key.getPermissions(), allowedActionAnnotation.action(),
                                                         hiveSecurityContext.getClientInetAddress(),
                                                         hiveSecurityContext.getOrigin());
            if (filtered.isEmpty()) {
                throw new HiveException(UNAUTHORIZED.getReasonPhrase(), UNAUTHORIZED.getStatusCode());
            }
            key.setPermissions(filtered);
            return context.proceed();
        } finally {
            ThreadLocalVariablesKeeper.clean();
        }
    }


}