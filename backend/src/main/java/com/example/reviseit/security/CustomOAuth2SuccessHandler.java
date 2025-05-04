package com.example.reviseit.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2SuccessHandler
  implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

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
    // Use the default Chrome extension redirect URI for OAuth2
    String redirectUrl = "https://hbmgdmnkggoanmngdhjkanbpnjnhjgfe.chromiumapp.org/?token=" + token;
    response.sendRedirect(redirectUrl);
  }
}
