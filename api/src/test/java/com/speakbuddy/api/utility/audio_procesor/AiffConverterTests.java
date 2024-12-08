package com.speakbuddy.api.utility.audio_procesor;

import com.speakbuddy.api.exception.InternalServerError;
import com.speakbuddy.api.utility.audio_processor.AiffConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class AiffConverterTests {

  @TempDir
  private static Path sharedTempDir;
  private final File mockWavFile = ResourceUtils.getFile("classpath:utility/audio_processor/compressed.wav");
  private final File mockCsvFile = ResourceUtils.getFile("classpath:utility/audio_processor/test.csv");
  private final File mockAiffFile = ResourceUtils.getFile("classpath:utility/audio_processor/success.aiff");
  private final Path outputFile = sharedTempDir.resolve("test_output.aiff");
  @InjectMocks
  private AiffConverter aiffConverter;

  AiffConverterTests() throws FileNotFoundException {
  }

  @Test
  void whenConvertToFile_returnSuccess() throws IOException {
    aiffConverter.convert(mockWavFile, outputFile.toFile());

    // -1 if no mismatch
    assertEquals(-1, Files.mismatch(mockAiffFile.toPath(), outputFile));
  }

  @Test
  void whenConvertToOutputStream_returnSuccess() throws IOException {
    final FileOutputStream outputStream = new FileOutputStream(outputFile.toFile());

    aiffConverter.convert(mockWavFile, outputStream);

    assertEquals(-1, Files.mismatch(mockAiffFile.toPath(), outputFile));
  }

  @Test
  void whenConvertToFile_throwUnsupportedAudio() {
    final var actual =
        assertThrows(InternalServerError.class, () -> aiffConverter.convert(mockCsvFile, outputFile.toFile()));

    assertEquals("Audio file not supported", actual.getMessage());
  }

}
