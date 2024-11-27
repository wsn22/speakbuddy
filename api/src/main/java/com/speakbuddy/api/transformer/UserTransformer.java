package com.speakbuddy.api.transformer;

import com.speakbuddy.api.controller.v1.response.CreateUserResponse;
import com.speakbuddy.api.controller.v1.response.UserDetailResponse;
import com.speakbuddy.api.database.repository.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTransformer {

  public static CreateUserResponse toCreateUserResponse(UserEntity userEntity) {
    return CreateUserResponse.builder()
        .userId(userEntity.getUserId())
        .username(userEntity.getUsername())
        .build();
  }

  public static UserDetailResponse toUserDetailResponse(UserEntity userEntity) {
    return UserDetailResponse.builder()
        .userId(userEntity.getUserId())
        .username(userEntity.getUsername())
        .build();
  }

}
