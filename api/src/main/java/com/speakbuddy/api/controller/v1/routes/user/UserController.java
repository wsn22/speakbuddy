package com.speakbuddy.api.controller.v1.routes.user;

import com.speakbuddy.api.controller.v1.request.CreateUserRequest;
import com.speakbuddy.api.controller.v1.response.CreateUserResponse;
import com.speakbuddy.api.controller.v1.response.UserDetailResponse;
import com.speakbuddy.api.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDetailResponse> getUserDetail(@PathVariable("userId") String userId) {
    return userService.getUserDetail(userId);
  }
}
