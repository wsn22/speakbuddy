package com.speakbuddy.api.utility.audio_processor;

import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.exception.InternalServerError;
import com.speakbuddy.api.utility.AudioProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

@Slf4j
public class AiffConverter implements AudioProcessor {

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

  @Override
  public void convert(File inputFile, OutputStream outputStream) {
    convert(inputFile, null, outputStream);
  }

  @Override
  public void convert(File inputFile, File outputFile) {
    convert(inputFile, outputFile, null);
  }

  public void convert(File file, File outputFile, OutputStream outputStream) {
    try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(file)) {
      originalStream.reset();
      if (outputFile != null) {
        AudioSystem.write(originalStream, AudioFileFormat.Type.WAVE, outputFile);
      } else if (outputStream != null) {
        AudioSystem.write(originalStream, AudioFileFormat.Type.AIFF, outputStream);
        outputStream.flush();
      }
    } catch (UnsupportedAudioFileException e) {
      log.error("UnsupportedAudioFileException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("Audio file not supported");
    } catch (IOException e) {
      log.error("IOException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("IO error");
    }
  }
}
