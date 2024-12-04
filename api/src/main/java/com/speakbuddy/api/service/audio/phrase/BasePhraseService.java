package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.audio_processor.AudioProcessorMapper;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class BasePhraseService {
  private final List<String> supportedFileTypes = List.of("wav", "aiff");

  protected String constructDirectory(String userId, String phraseId) {
    return String.format("%s/%s", userId, phraseId);
  }

  protected void validateSupportedFileType(String filename) {
    if (!supportedFileTypes.contains(filename)) {
      log.info("Requested filename: {}", filename);
      throw new FileProcessorException("Requested file type not supported");
    }
  }

  protected File saveTempFile(String fileName, MultipartFile audioFile) {
    final File tempFile = new LocalFileProcessor()
        .storeFile("temp", fileName);

    try {
      audioFile.transferTo(tempFile.getAbsoluteFile());
      return tempFile;
    } catch (IOException e) {
      log.error("IOException on save temp file.", e);
      throw new FileProcessorException("Error save temporary file. Err: " + e.getMessage());
    }
  }

  protected File saveFile(File tempFile, String targetFileName, String userId, String phraseId) {
    final String destinationPath = constructDirectory(userId, phraseId);

    final File destinationFile = new LocalFileProcessor("audio")
        .storeFile(destinationPath, targetFileName);

    new AudioProcessorMapper().getAudioProcessor("wav")
        .convert(tempFile, destinationFile);

    return destinationFile;
  }
}
