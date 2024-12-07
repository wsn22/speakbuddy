package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.utility.audio_processor.AudioProcessorMapper;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BasePhraseServiceTests {

  private final String phraseId = "phrase-id-test";
  private final String userId = "user-id-test";
  private final File mockWavFile = ResourceUtils.getFile("classpath:service/audio/phrase/phrase_service/success.wav");
  @Mock
  private LocalFileProcessor localFileProcessor;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private AudioProcessorMapper audioProcessorMapper;
  @InjectMocks
  private BasePhraseService basePhraseService;

  BasePhraseServiceTests() throws FileNotFoundException {
  }

  @Test
  void whenSaveTempFile_returnSuccess() throws IOException {
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "success.wav", "audio/wav", new FileInputStream(mockWavFile));

    when(localFileProcessor.storeFile(anyString(), anyString())).thenReturn(mockWavFile);

    final File tempFile = basePhraseService.saveTempFile("success.wav", mockMultipartFile);

    assertEquals(mockWavFile, tempFile);
    verify(localFileProcessor).storeFile("temp", "success.wav");
  }

  @Test
  void whenConvertAndSaveFile_returnSuccess() {
    final File tempFile = new File("temp.wav");
    final String targetFileName = "success.wav";
    final String destinationPath = "audio/user-id-test/phrase-id-test";

    when(localFileProcessor.storeFile(any(), any())).thenReturn(tempFile);

    final File actual = basePhraseService.convertAndSaveFile(tempFile, targetFileName, userId, phraseId);

    assertEquals(tempFile, actual);

    verify(localFileProcessor).storeFile(destinationPath, targetFileName);
    verify(audioProcessorMapper.getAudioProcessor("wav")).convert(tempFile, tempFile);
  }

  @Test
  void whenGetStreamingResponseBody_returnSuccess() throws IOException {
    final File mockFile = ResourceUtils.getFile("classpath:service/audio/phrase/phrase_service/success.wav");
    final StreamingResponseBody mockResponse = outputStream -> Files.copy(mockFile.toPath(), outputStream);

    final String filePath = "/root-path/audio/user-id-test/phrase/phrase-id-test/timestamp.wav";

    when(localFileProcessor.getFile(any())).thenReturn(mockFile);

    final StreamingResponseBody actual = basePhraseService.getStreamingResponseBody(filePath, "wav");

    // TODO: Equalization with streamingResponseBody
    //assertEquals(mockResponse, actual);
    verify(localFileProcessor).getFile(filePath);
    //verify(audioProcessorMapper.getAudioProcessor("wav")).convert(eq(mockFile), any(OutputStream.class));
  }

}
