package com.speakbuddy.api.utility.impl;

import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.EntityNotFoundException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.isConversionSupported;

@Slf4j
public class LocalFileProcessor implements FileUtility {

  private final String relativePath;

  public LocalFileProcessor(String relativePath) {
    this.relativePath = relativePath;
  }

  @Override
  public String storeFile(File file, String targetPath, String targetFileName) {
    //System.out.println(file.getAbsoluteFile());
    //if (file.isFile()) {
    //  throw new BadRequestException("File is not exists.");
    //}

    final String aliasPath = String.format("%s/%s", targetPath, targetFileName);
    final Path destinationPath = Paths.get("..", relativePath, targetPath).normalize();

    try {
      Files.createDirectories(destinationPath.toAbsolutePath());
    } catch (IOException e) {
      log.error("[storeFile] create directory fail", e);
      throw new FileProcessorException("Directory creation failed");
    }

    File outputFile = destinationPath.resolve(targetFileName).toFile();

    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {

      //audioInputStream.reset();
      //AudioFileFormat hehehe = AudioSystem.getAudioFileFormat(file.getInputStream());
      //System.out.println(hehehe.getType());

      AudioFormat sourceFormat = audioInputStream.getFormat();
      System.out.println(sourceFormat);
      AudioFormat targetFormat = new AudioFormat(
          (float) 8000.0,
          8,
          1,
          true,
          false
      );

      isConversionSupported(targetFormat, sourceFormat);

      try (AudioInputStream wavStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream)) {
        AudioSystem.write(wavStream, AudioFileFormat.Type.WAVE, outputFile);
      }

      //try (;
      //     FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
      //  byte[] buffer = new byte[4096];
      //  int bytesRead;
      //  while ((bytesRead = audioInputStream.read(buffer)) != -1) {
      //    fileOutputStream.write(buffer, 0, bytesRead);
      //  }
    } catch (UnsupportedAudioFileException e) {
      log.error("Error processing audio file", e);
      throw new FileProcessorException("Error file creation. Err: " + e.getMessage());
    } catch (IOException e) {
      log.error("Error read audio file", e);
      throw new FileProcessorException("Error read audio file. Err: " + e.getMessage());
    }


    //try (InputStream inputStream = file.getInputStream()) {
    // https://stackoverflow.com/questions/5529754/java-io-ioexception-mark-reset-not-supported
    //final InputStream bufferedIn = new BufferedInputStream(inputStream);
    //final AudioInputStream originalAudioStream = AudioSystem.getAudioInputStream(bufferedIn);
    //AudioSystem.write(originalAudioStream, AudioFileFormat.Type.WAVE, destinationPath.toFile());
    //  Files.copy(inputStream, destinationPath.toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
    //} catch (IOException e) {
    //  log.error("[storeFile] fail save data with target: {}", destinationPath, e);
    //  throw new FileProcessorException("File save failed");
    //} catch (UnsupportedAudioFileException e) {
    //  e.printStackTrace();  // TODO impl
    //}

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
