package com.speakbuddy.api.utility.impl;

import com.speakbuddy.api.exception.EntityNotFoundException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class LocalFileProcessor implements FileUtility {

  @Override
  public File storeFile(String targetPath, String targetFileName) {
    final String rootPath = "files";
    final Path destinationPath = Paths.get("..", rootPath, targetPath).normalize();

    try {

      Files.createDirectories(destinationPath.toAbsolutePath());
      final File outputFile = destinationPath.resolve(targetFileName).toFile();
      if (outputFile.createNewFile()) {
        return outputFile;
      }

    } catch (IOException e) {
      log.error("[storeFile] create directory fail", e);
      throw new FileProcessorException("Directory creation failed");
    }

    throw new FileProcessorException("File creation failed");
  }

  @Override
  public File getFile(String filePath) {
    final File destinationFile = new File(filePath);
    if (!destinationFile.exists()) {
      log.warn("File {} not found", destinationFile);
      throw new FileProcessorException("Requested file not found");
    }
    return destinationFile;
  }

  @Override
  public void deleteFile(String filePath) {
    final File targetFile = Paths.get("..", filePath).normalize().toAbsolutePath().toFile();

    if (!targetFile.exists()) {
      throw new EntityNotFoundException("File not found");
    } else if (!targetFile.delete()) {
      throw new FileProcessorException("File delete failed");
    }
  }
}
