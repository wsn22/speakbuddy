package com.speakbuddy.api.utility;

import com.speakbuddy.api.exception.FileProcessorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

@Slf4j
public class AudioConverter {

  public boolean isCompatible(File file) {
    try {
      AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
      AudioFileFormat.Type fileType = fileFormat.getType();
      
      try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
        audioInputStream.reset();
        return AudioSystem.isFileTypeSupported(fileType, audioInputStream);
      }


    } catch (UnsupportedAudioFileException e) {
      log.error("Error processing audio file", e);
      throw new FileProcessorException("Error file creation. Err: " + e.getMessage());
    } catch (IOException e) {
      log.error("huee", e);
      throw new FileProcessorException("Error read audio file. Err: " + e.getMessage());
    }
  }
}
