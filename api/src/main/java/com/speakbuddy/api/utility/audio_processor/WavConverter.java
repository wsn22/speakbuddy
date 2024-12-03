package com.speakbuddy.api.utility.audio_processor;

import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.exception.InternalServerError;
import com.speakbuddy.api.utility.AudioProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.isConversionSupported;

@Slf4j
public class WavConverter implements AudioProcessor {

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
  public void convert(OutputStream outputStream, File file) {
    // converter not worked when I need too .write to outputStream
    final AudioFormat targetFormat = new AudioFormat(
        (float) 8000.0,
        8,
        1,
        true,
        false
    );

    File outputFile = new File("/Users/wicaksno/code/speakbuddy/wisnu/temp/output.wav");

    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
         AudioInputStream convertedAudioStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream)) {
      AudioFormat sourceFormat = audioInputStream.getFormat();
      log.info("isConversionSupported: {}", isConversionSupported(targetFormat, sourceFormat));
      AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputStream);
      outputStream.flush();

    } catch (UnsupportedAudioFileException e) {
      log.error("UnsupportedAudioFileException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("Audio file not supported");
    } catch (IOException e) {
      log.error("IOException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("IO error");
    }
  }
}
