package com.example.reviseit.service;

import com.example.reviseit.model.User;

public interface UserService {
  User findOrCreateUser(String email);
}
