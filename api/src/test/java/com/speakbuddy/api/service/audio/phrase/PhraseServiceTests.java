package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.controller.v1.response.SimpleResponse;
import com.speakbuddy.api.database.manager.PhraseManager;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static com.speakbuddy.api.utility.TestHelper.getObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PhraseServiceTests {

  private final String phraseId = "phrase-id-test";
  private final String userId = "user-id-test";
  private final File mockWavFile = ResourceUtils.getFile("classpath:service/audio/phrase/phrase_service/success.wav");
  @Mock
  private PhraseManager phraseManager;
  @Spy
  @InjectMocks
  private PhraseService phraseService;

  PhraseServiceTests() throws FileNotFoundException {
  }

  @Test
  void whenSavePhrase_thenBadRequest() throws IOException {
    final File mockFile = ResourceUtils.getFile("classpath:service/audio/phrase/phrase_service/test.csv");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", "text/csv", new FileInputStream(mockFile));

    final var actual = assertThrows(BadRequestException.class, () -> phraseService.savePhrase("user-id-test", "phrase-id-test", mockMultipartFile));

    assertEquals("Requested file type not supported", actual.getMessage());
  }

  @Test
  void whenSavePhrase_thenOk() throws IOException {
    final File mockCreatedFile = new File("1733577740163.wav");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "success.wav", "audio/wav", new FileInputStream(mockWavFile));

    doReturn(mockCreatedFile).when(phraseService).saveTempFile(anyString(), any());
    doReturn(mockWavFile).when(phraseService).convertAndSaveFile(any(), anyString(), anyString(), anyString());

    final var actual = phraseService.savePhrase(userId, phraseId, mockMultipartFile);

    assertEquals(new SimpleResponse("File successfully saved"), actual.getBody());

    verify(phraseService).saveTempFile(anyString(), eq(mockMultipartFile));
    verify(phraseService).convertAndSaveFile(eq(mockCreatedFile), anyString(), eq(userId), eq(phraseId));
    verify(phraseManager).upsert(eq(new PhraseIds(userId, phraseId)), anyString());
  }

  @Test
  void whenGetPhrase_thenBadRequest() {
    final var actual = assertThrows(BadRequestException.class, () -> phraseService.getPhrase("user-id-test", "phrase-id-test", "csv"));

    assertEquals("Requested file type not supported", actual.getMessage());
  }

  @Test
  void whenGetPhrase_thenOk() throws IOException {
    final PhraseEntity mockEntity = getObject("service/audio/phrase/phrase_service/phrase_entity.json", PhraseEntity.class);
    final StreamingResponseBody mockResponse = outputStream -> Files.copy(mockWavFile.toPath(), outputStream);

    when(phraseManager.findById(any())).thenReturn(mockEntity);
    doReturn(mockResponse).when(phraseService).getStreamingResponseBody(any(), anyString());

    final var actual = phraseService.getPhrase(userId, phraseId, "wav");

    assertEquals(mockResponse, actual.getBody());
    assertEquals("attachment; filename=timestamp.wav", actual.getHeaders().get("Content-Disposition").getFirst());
    assertEquals("audio/wav", actual.getHeaders().get("Content-Type").getFirst());

    verify(phraseManager).findById(new PhraseIds(userId, phraseId));
    verify(phraseService).getStreamingResponseBody(mockEntity.getFilePath(), "wav");
  }
}
