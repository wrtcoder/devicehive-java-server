package com.devicehive.resource;

import com.devicehive.base.AbstractResourceTest;
import com.devicehive.model.enums.UserRole;
import com.devicehive.model.enums.UserStatus;
import com.devicehive.model.updates.UserUpdate;
import com.devicehive.vo.UserVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserResourceTest extends AbstractResourceTest {

    @Test
    public void should_return_error_code_if_user_is_disabled_for_basic_auth() throws Exception {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        UserUpdate testUser = new UserUpdate();
        testUser.setLogin(Optional.ofNullable(login));
        testUser.setRole(Optional.ofNullable(UserRole.CLIENT.getValue()));
        testUser.setPassword(Optional.ofNullable(password));
        testUser.setStatus(Optional.ofNullable(UserStatus.ACTIVE.getValue()));

        UserVO user = performRequest("/user", "POST", emptyMap(), singletonMap(HttpHeaders.AUTHORIZATION, basicAuthHeader(ADMIN_LOGIN, ADMIN_PASS)), testUser, CREATED, UserVO.class);
        assertThat(user.getId(), notNullValue());

        final long userid = user.getId();
        user = performRequest("/user/" + user.getId(), "GET", emptyMap(), singletonMap(HttpHeaders.AUTHORIZATION, basicAuthHeader(ADMIN_LOGIN, ADMIN_PASS)), null, OK, UserVO.class);
        assertThat(user.getStatus(), equalTo(UserStatus.ACTIVE));
        assertThat(user.getId(), equalTo(userid));

        testUser = new UserUpdate();
        testUser.setStatus(Optional.ofNullable(UserStatus.DISABLED.getValue()));
        testUser.setLogin(Optional.ofNullable(login));
        testUser.setPassword(Optional.ofNullable(password));
        performRequest("/user/" + user.getId(), "PUT", emptyMap(), singletonMap(HttpHeaders.AUTHORIZATION, basicAuthHeader(ADMIN_LOGIN, ADMIN_PASS)), testUser, NO_CONTENT, Response.class);

        user = performRequest("/user/" + user.getId(), "GET", emptyMap(), singletonMap(HttpHeaders.AUTHORIZATION, basicAuthHeader(ADMIN_LOGIN, ADMIN_PASS)), null, OK, UserVO.class);
        assertThat(user.getStatus(), equalTo(UserStatus.DISABLED));

        testUser = new UserUpdate();
        testUser.setStatus(Optional.ofNullable(UserStatus.ACTIVE.getValue()));
        testUser.setLogin(Optional.ofNullable(login));
        testUser.setPassword(Optional.ofNullable(password));
        performRequest("/user/current", "PUT", emptyMap(), singletonMap(HttpHeaders.AUTHORIZATION, basicAuthHeader(login, password)), testUser, UNAUTHORIZED, Response.class);
    }

}
