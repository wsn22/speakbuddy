package com.speakbuddy.api.service.user;

import com.speakbuddy.api.controller.v1.request.CreateUserRequest;
import com.speakbuddy.api.controller.v1.response.CreateUserResponse;
import com.speakbuddy.api.controller.v1.response.UserDetailResponse;
import com.speakbuddy.api.database.manager.UserManager;
import com.speakbuddy.api.database.repository.entity.UserEntity;
import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.EntityNotFoundException;
import com.speakbuddy.api.transformer.UserTransformer;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  private final UserManager userManager;

  public UserService(UserManager userManager) {
    this.userManager = userManager;
  }

  /**
   * Create new user
   *
   * @param userRequest user request
   * @return create user response
   */
  public ResponseEntity<CreateUserResponse> createUser(CreateUserRequest userRequest) {
    final Optional<UserEntity> existsUsers = userManager.findBy(Example.of(UserEntity.builder().username(userRequest.getUsername()).build()));

    if (existsUsers.isPresent()) {
      throw new BadRequestException("User already exists");
    }

    final UserEntity newUser = userManager.insert(UserEntity.builder().username(userRequest.getUsername()).build());

    return ResponseEntity.ok(UserTransformer.toCreateUserResponse(newUser));
  }

  /**
   * Get user detail by id
   *
   * @param idUser user id
   * @return user detail response
   */
  public ResponseEntity<UserDetailResponse> getUserDetail(String idUser) {
    Optional<UserEntity> existsUser = userManager.findById(idUser);

    if (existsUser.isEmpty()) {
      throw new EntityNotFoundException("User not found");
    }

    return ResponseEntity.ok(
        UserTransformer.toUserDetailResponse(existsUser.get())
    );
  }
}
