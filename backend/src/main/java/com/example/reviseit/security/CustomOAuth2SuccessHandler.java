package com.example.reviseit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2SuccessHandler
  implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  @Value("${extension.id}")
  private String ID;

  public CustomOAuth2SuccessHandler(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    String token = jwtUtil.generateToken(email);

    // Try to extract redirect_uri from state (base64-encoded JSON)
    String state = request.getParameter("state");
    String redirectUri = null;
    if (state != null) {
      try {
        String json = new String(Base64.getUrlDecoder().decode(state));
        ObjectMapper mapper = new ObjectMapper();
        redirectUri = mapper.readTree(json).path("redirect_uri").asText(null);
      } catch (Exception e) {
        // fallback to null
      }
    }
    String redirectUrl;
    if (redirectUri != null && !redirectUri.isEmpty()) {
      redirectUrl =
        redirectUri +
        (redirectUri.contains("?") ? "&" : "?") +
        "token=" +
        token;
    } else {
      redirectUrl =
        "https://" + ID + ".chromiumapp.org/?token=" +
        token;
    }
    response.sendRedirect(redirectUrl);
  }
}
