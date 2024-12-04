package com.speakbuddy.api.service.user;

import com.speakbuddy.api.controller.v1.request.CreateUserRequest;
import com.speakbuddy.api.controller.v1.response.CreateUserResponse;
import com.speakbuddy.api.controller.v1.response.UserDetailResponse;
import com.speakbuddy.api.database.manager.UserManager;
import com.speakbuddy.api.database.repository.entity.UserEntity;
import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTests {

  @Mock
  private UserManager userManager;

  @InjectMocks
  private UserService userService;

  @Test
  void whenCreateUser_thenSuccess() {
    when(userManager.findBy(any())).thenReturn(Optional.empty());
    when(userManager.insert(any())).thenReturn(new UserEntity());

    final var actual = userService.createUser(new CreateUserRequest());

    assertEquals(new CreateUserResponse(), actual.getBody());

    verify(userManager).findBy(any());
    verify(userManager).insert(any());
  }

  @Test
  void whenCreateUser_userAlreadyExists_returnBadRequest() {
    when(userManager.findBy(any())).thenReturn(Optional.of(new UserEntity()));

    final var actual = assertThrows(BadRequestException.class, () -> userService.createUser(new CreateUserRequest()));

    assertEquals("User already exists", actual.getMessage());

    verify(userManager).findBy(any());
    verify(userManager, never()).insert(any());
  }

  @Test
  void whenGetUserDetail_returnSuccess() {
    when(userManager.findById(any())).thenReturn(Optional.of(new UserEntity()));

    final var actual = userService.getUserDetail("any-id");

    assertEquals(new UserDetailResponse(), actual.getBody());

    verify(userManager).findById(any());
  }

  @Test
  void whenGetUserDetail_returnNotFound() {
    when(userManager.findById(any())).thenReturn(Optional.empty());

    final var actual = assertThrows(EntityNotFoundException.class, () -> userService.getUserDetail("any-id"));

    assertEquals("User not found", actual.getMessage());

    verify(userManager).findById(any());
  }
}
