package com.speakbuddy.api.controller.v1.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class UserDetailResponse {
  private String userId;
  private String username;
}
