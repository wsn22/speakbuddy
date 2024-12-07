package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.FileUtility;
import com.speakbuddy.api.utility.audio_processor.AudioProcessorMapper;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class BasePhraseService {
  private final List<String> supportedFileTypes = List.of("wav", "aiff");
  private final FileUtility localFileProcessor;
  private final AudioProcessorMapper audioProcessorMapper;

  public BasePhraseService(LocalFileProcessor localFileProcessor, AudioProcessorMapper audioProcessorMapper) {
    this.localFileProcessor = localFileProcessor;
    this.audioProcessorMapper = audioProcessorMapper;
  }

  protected String constructDirectory(String userId, String phraseId) {
    return String.format("audio/%s/%s", userId, phraseId);
  }

  protected void validateSupportedFileType(String filename) {
    if (!supportedFileTypes.contains(filename)) {
      log.info("Requested filename: {}", filename);
      throw new BadRequestException("Requested file type not supported");
    }
  }

  protected File saveTempFile(String fileName, MultipartFile audioFile) {
    final File tempFile = localFileProcessor
        .storeFile("temp", fileName);

    try {
      audioFile.transferTo(tempFile.getAbsoluteFile());
      return tempFile;
    } catch (IOException e) {
      log.error("IOException on save temp file.", e);
      throw new FileProcessorException("Error save temporary file. Err: " + e.getMessage());
    }
  }

  protected File convertAndSaveFile(File tempFile, String targetFileName, String userId, String phraseId) {
    final String destinationPath = constructDirectory(userId, phraseId);

    final File destinationFile = localFileProcessor.storeFile(destinationPath, targetFileName);

    audioProcessorMapper.getAudioProcessor("wav")
        .convert(tempFile, destinationFile);

    return destinationFile;
  }

  protected StreamingResponseBody getStreamingResponseBody(String filePath, String audioFormat) {
    final File myAudioFile = localFileProcessor.getFile(filePath);

    return outputStream -> {
      try {
        audioProcessorMapper.getAudioProcessor(audioFormat)
            .convert(myAudioFile, outputStream);
      } catch (Exception e) {
        log.error("Error on convert file to output stream", e);
        throw new FileProcessorException("Error on convert file to output stream. Err: " + e.getMessage());
      }
    };
  }
}
