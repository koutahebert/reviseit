package com.example.reviseit.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

public class CustomAuthorizationRequestResolver
  implements OAuth2AuthorizationRequestResolver {

  private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

  public CustomAuthorizationRequestResolver(
    org.springframework.security.oauth2.client.registration.ClientRegistrationRepository repo,
    String authorizationRequestBaseUri
  ) {
    this.defaultResolver =
      new DefaultOAuth2AuthorizationRequestResolver(
        repo,
        authorizationRequestBaseUri
      );
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
    return customizeAuthorizationRequest(request, req);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(
    HttpServletRequest request,
    String clientRegistrationId
  ) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(
      request,
      clientRegistrationId
    );
    return customizeAuthorizationRequest(request, req);
  }

  private OAuth2AuthorizationRequest customizeAuthorizationRequest(
    HttpServletRequest request,
    OAuth2AuthorizationRequest req
  ) {
    if (req == null) return null;
    String redirectUri = request.getParameter("redirect_uri");
    if (StringUtils.hasText(redirectUri)) {
      // Store redirect_uri in state (as JSON, base64 encoded)
      Map<String, Object> stateMap = new HashMap<>();
      stateMap.put("redirect_uri", redirectUri);
      String stateJson = "{\"redirect_uri\":\"" + redirectUri + "\"}";
      String encodedState = Base64
        .getUrlEncoder()
        .encodeToString(stateJson.getBytes());
      return OAuth2AuthorizationRequest.from(req).state(encodedState).build();
    }
    return req;
  }
}
