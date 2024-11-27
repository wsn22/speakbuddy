package com.speakbuddy.api.database.manager;

import com.speakbuddy.api.database.repository.UserRepository;
import com.speakbuddy.api.database.repository.entity.UserEntity;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserManager {
  private final UserRepository userRepository;

  public UserManager(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<UserEntity> findById(String id) {
    return userRepository.findById(id);
  }

  public Optional<UserEntity> findBy(Example<UserEntity> byUserEntity) {
    return userRepository.findOne(byUserEntity);
  }

  public UserEntity insert(UserEntity newUserEntity) {
    newUserEntity.setUserId(UUID.randomUUID().toString());
    return userRepository.save(newUserEntity);
  }
}
