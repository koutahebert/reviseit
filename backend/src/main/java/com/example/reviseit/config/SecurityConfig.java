package com.example.reviseit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableAsync
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception {
    http
      .authorizeHttpRequests(auth ->
        auth // Use authorizeHttpRequests instead of authorizeRequests
          .requestMatchers("/api/bookmarks/**")
          .authenticated() // Use requestMatchers instead of antMatchers
          .anyRequest()
          .permitAll()
      )
      .oauth2Login(oauth2 ->
        oauth2.userInfoEndpoint(userInfo ->
          userInfo.userService(oauth2UserService())
        )
      )
      .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, OAuth2User> oauth2UserService() {
    return userRequest -> {
      DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        // email attribute guaranteed by Google
      return delegate.loadUser(userRequest);
    };
  }
}
