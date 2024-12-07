package com.speakbuddy.api.utility.audio_processor;

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

  @Override
  public void convert(File inputFile, OutputStream outputStream) {
    convert(inputFile, null, outputStream);
  }

  @Override
  public void convert(File inputFile, File outputFile) {
    convert(inputFile, outputFile, null);
  }

  private void convert(File file, File outputFile, OutputStream outputStream) {
    final AudioFormat targetFormat = new AudioFormat(
        (float) 8000.0,
        8,
        1,
        true,
        false
    );

    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
         AudioInputStream convertedAudioStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream)) {
      AudioFormat sourceFormat = audioInputStream.getFormat();
      log.info("isConversionSupported: {}", isConversionSupported(targetFormat, sourceFormat));

      if (outputFile != null) {
        AudioSystem.write(convertedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
      } else if (outputStream != null) {
        AudioSystem.write(convertedAudioStream, AudioFileFormat.Type.WAVE, outputStream);
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
