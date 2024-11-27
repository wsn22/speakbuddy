package com.speakbuddy.api.configuration.controller;

import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

  @ExceptionHandler( {NotFoundException.class})
  public ResponseEntity<Object> handleNotFoundException(Exception ex, ServletWebRequest request) {
    final ErrorMessage bodyMessage = new ErrorMessage(ex.getMessage(), ex.getClass().getName(), request.getRequest().getRequestURI(), HttpStatus.NOT_FOUND.value());
    return handleExceptionInternal(ex, bodyMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler( {BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestExceptions(Exception ex, ServletWebRequest request) {
    final ErrorMessage bodyMessage = new ErrorMessage(ex.getMessage(), ex.getClass().getName(), request.getRequest().getRequestURI(), HttpStatus.BAD_REQUEST.value());
    return handleExceptionInternal(ex, bodyMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
