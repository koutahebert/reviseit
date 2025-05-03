package com.example.reviseit.service;

import com.example.reviseit.model.User;
import com.example.reviseit.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW) // Force a new, independent transaction
  public User findOrCreateUser(String email) {
    System.err.println(
      "UserService: findOrCreateUser called with email: " + email
    );
    Optional<User> existingUser = userRepository.findByEmail(email);

    if (existingUser.isPresent()) {
      System.err.println("UserService: Found existing user: " + email);
      return existingUser.get();
    } else {
      System.err.println(
        "UserService: Attempting to create new user: " + email
      );
      User newUser = new User();
      newUser.setEmail(email);
      try {
        System.err.println("UserService: Saving new user to DB...");
        User savedUser = userRepository.saveAndFlush(newUser); // Use saveAndFlush
        System.err.println(
          "UserService: Successfully created new user with ID: " +
          savedUser.getId() +
          " for email: " +
          email
        );
        return savedUser;
      } catch (Exception e) {
        // Log the error critically
        System.err.println(
          "CRITICAL: UserService failed to save new user with email: " +
          email +
          " - Error: " +
          e.getMessage()
        );
        e.printStackTrace();
        // Re-throw a specific runtime exception
        throw new RuntimeException("Failed to create user: " + email, e);
      }
    }
  }
}
