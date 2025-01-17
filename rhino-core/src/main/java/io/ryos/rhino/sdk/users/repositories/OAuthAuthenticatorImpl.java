/*
 * Copyright 2018 Ryos.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ryos.rhino.sdk.users.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ryos.rhino.sdk.SimulationConfig;
import io.ryos.rhino.sdk.exceptions.ExceptionUtils;
import io.ryos.rhino.sdk.exceptions.UserLoginException;
import io.ryos.rhino.sdk.users.OAuthEntity;
import io.ryos.rhino.sdk.users.data.OAuthUser;
import io.ryos.rhino.sdk.users.data.OAuthUserImpl;
import io.ryos.rhino.sdk.users.data.User;
import java.util.Optional;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response.Status;

/**
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public class OAuthAuthenticatorImpl implements Authenticator<OAuthUser> {

  private static final String CLIENT_ID = "client_id";
  private static final String CLIENT_SECRET = "client_secret";
  private static final String GRANT_TYPE = "grant_type";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String SCOPE = "scope";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public OAuthUser authenticate(User user) {

    try {
      var form = new Form();

      if (SimulationConfig.getClientId() != null) {
        form.param(CLIENT_ID, SimulationConfig.getClientId());
      }

      if (SimulationConfig.getClientSecret() != null) {
        form.param(CLIENT_SECRET, SimulationConfig.getClientSecret());
      }

      if (SimulationConfig.getGrantType() != null) {
        form.param(GRANT_TYPE, SimulationConfig.getGrantType());
      }

      if (user.getScope() != null) {
        form.param(SCOPE, user.getScope());
      }

      if (user.getPassword() != null) {
        form.param(PASSWORD, user.getPassword());
      }

      form.param(USERNAME, user.getUsername());

      var client = ClientBuilder.newClient();
      var response = client
          .target(SimulationConfig.getAuthServer())
          .request()
          .post(Entity.form(form));

      if (response.getStatus() != Status.OK.getStatusCode()) {
        System.out.println(
            "Cannot login user, status=" + response.getStatus() + ", message=" + response
                .readEntity(String.class));
        return null;
      }

      var s = response.readEntity(String.class);

      var entity = mapToEntity(s);

      return new OAuthUserImpl(user.getUsername(),
          user.getPassword(),
          entity.getAccessToken(),
          entity.getRefreshToken(),
          user.getScope(),
          SimulationConfig.getClientId(),
          user.getId(),
          user.getRegion());
    } catch (Exception e) {
      ExceptionUtils.rethrow(e, UserLoginException.class, "Login failed.");
    }

    return null;
  }

  private OAuthEntity mapToEntity(final String s) {
    final OAuthEntity o;
    try {
      o = objectMapper.readValue(s, OAuthEntity.class);
    } catch (Throwable t) {
      throw new RuntimeException(
          "Cannot map authorization server response to entity type: " + OAuthEntity.class.getName(),
          t);
    }
    return o;
  }

}
