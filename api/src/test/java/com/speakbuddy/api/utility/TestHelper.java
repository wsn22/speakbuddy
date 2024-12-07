package com.speakbuddy.api.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.List;

public class TestHelper {
  public static final ObjectMapper objectMapper = new ObjectMapper();
  public static final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

  private TestHelper() {
  }

  public static <T> List<T> getObjects(String path, Class<T> clazz) throws IOException {
    return objectMapper.readValue(resolver.getResource("classpath:" + path).getFile(),
        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
  }

  public static <T> T getObject(String path, Class<T> clazz) throws IOException {
    return objectMapper.readValue(resolver.getResource("classpath:" + path).getFile(),
        objectMapper.getTypeFactory().constructType(clazz));
  }
}
