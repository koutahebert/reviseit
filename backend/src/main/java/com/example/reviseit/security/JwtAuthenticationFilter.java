package com.example.reviseit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      try {
        username = jwtUtil.extractUsername(token);
      } catch (Exception e) {
        // Invalid token
      }
    }

    if (
      username != null &&
      SecurityContextHolder.getContext().getAuthentication() == null
    ) {
      if (jwtUtil.isTokenValid(token, username)) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          new User(username, "", Collections.emptyList()),
          null,
          Collections.emptyList()
        );
        authentication.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }
}
