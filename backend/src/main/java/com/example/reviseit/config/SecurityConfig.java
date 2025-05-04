package com.example.reviseit.config;

import com.example.reviseit.model.User; // Import User model
import com.example.reviseit.repository.UserRepository; // Import UserRepository
import com.example.reviseit.security.CustomOAuth2SuccessHandler; // Import CustomOAuth2SuccessHandler
import com.example.reviseit.security.JwtAuthenticationFilter; // Import JwtAuthenticationFilter
import com.example.reviseit.service.UserService; // Import UserService
import jakarta.annotation.PostConstruct; // Import PostConstruct
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest; // Import OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService; // Import OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException; // Import OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error; // Import OAuth2Error
import org.springframework.security.oauth2.core.user.DefaultOAuth2User; // Import DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@EnableAsync
public class SecurityConfig {

  private final UserService userService;
  private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  public SecurityConfig(
    UserService userService,
    CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
    JwtAuthenticationFilter jwtAuthenticationFilter
  ) {
    this.userService = userService;
    this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    System.out.println(
      "SecurityConfig CONSTRUCTOR: This config is being used!"
    );
  }

  // Add PostConstruct method for logging
  @PostConstruct
  public void checkUserServiceInjection() {
    if (this.userService == null) {
      System.err.println(
        "!!! SecurityConfig PostConstruct: userService is NULL !!!"
      );
    } else {
      System.out.println(
        "+++ SecurityConfig PostConstruct: userService is successfully injected. +++"
      );
    }
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception { // Removed injected parameters
    System.out.println("--- Configuring SecurityFilterChain ---"); // Log before oauth2Login
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS globally
      .authorizeHttpRequests(auth ->
        auth // Use authorizeHttpRequests instead of authorizeRequests
          .requestMatchers("/api/**") // Apply authentication to all /api/** paths
          .authenticated()
          .anyRequest()
          .permitAll()
      )
      .addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
      )
      .exceptionHandling(e ->
        e.defaultAuthenticationEntryPointFor(
          new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), // Return 401 for API requests
          new AntPathRequestMatcher("/api/**")
        )
      )
      .oauth2Login(oauth2 -> {
        System.out.println("--- Applying .oauth2Login() configuration ---");
        oauth2
          .userInfoEndpoint(userInfo ->
            userInfo.userService(customOAuth2UserService())
          )
          .successHandler(customOAuth2SuccessHandler);
      })
      .csrf(AbstractHttpConfigurer::disable);
    System.out.println("--- SecurityFilterChain configuration complete ---"); // Log after configuration
    return http.build();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
    return userRequest -> {
      System.out.println("CustomOAuth2UserService: Called!");
      DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
      OAuth2User oauth2User = delegate.loadUser(userRequest);
      String email = oauth2User.getAttribute("email");
      if (email == null) {
        System.err.println("CustomOAuth2UserService: Email attribute is null!");
        throw new OAuth2AuthenticationException("Email attribute missing");
      }
      System.out.println(
        "CustomOAuth2UserService: Creating or finding user for: " + email
      );
      userService.findOrCreateUser(email);
      return oauth2User;
    };
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow frontend origin
    configuration.setAllowedMethods(
      Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
    ); // Allow common methods
    configuration.setAllowedHeaders(
      Arrays.asList("Authorization", "Cache-Control", "Content-Type")
    );
    configuration.setAllowCredentials(true); // Allow credentials
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Apply CORS to all paths ("/**")
    return source;
  }
}
