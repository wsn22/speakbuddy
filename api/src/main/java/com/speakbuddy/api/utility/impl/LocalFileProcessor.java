package com.speakbuddy.api.utility.impl;

import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class LocalFileProcessor implements FileUtility {

  private final String relativePath;

  public LocalFileProcessor(String relativePath) {
    this.relativePath = relativePath;
  }

  @Override
  public String storeFile(MultipartFile file, String targetPath, String targetFileName) {
    if (file.isEmpty()) {
      throw new BadRequestException("File is empty.");
    }

    final String aliasPath = String.format("%s/%s", targetPath, targetFileName);
    final Path destinationPath = Paths.get("..", relativePath, aliasPath).normalize();

    try {
      Files.createDirectories(destinationPath.toAbsolutePath());
    } catch (IOException e) {
      log.error("[storeFile] create directory fail", e);
      throw new FileProcessorException("Directory creation failed");
    }

    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, destinationPath.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error("[storeFile] fail save data with target: {}", destinationPath, e);
      throw new FileProcessorException("File save failed");
    }

    return aliasPath;
  }

  @Override
  public InputStream getFile(String filePath) throws IOException {
    final Path destinationPath = Paths.get("..", relativePath, filePath).normalize().toAbsolutePath();

    return Files.newInputStream(destinationPath);

    //try (InputStream inputStream = Files.newInputStream(destinationPath)) {
    //  return inputStream;
    //} catch (IOException e) {
    //  log.error("[getFile] fail read file with path: {}", filePath, e);
    //  throw new FileProcessorException("File read failed");
    //}

  }
}
