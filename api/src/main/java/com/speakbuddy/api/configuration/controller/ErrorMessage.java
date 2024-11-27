package com.speakbuddy.api.configuration.controller;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ErrorMessage {
  private String message;
  private String error;
  private String path;
  private Integer status;
}
