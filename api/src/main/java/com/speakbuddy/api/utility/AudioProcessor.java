package com.speakbuddy.api.utility;

import com.speakbuddy.api.exception.FileProcessorException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public interface AudioProcessor {
  void convert(OutputStream outputStream, File file);
}
