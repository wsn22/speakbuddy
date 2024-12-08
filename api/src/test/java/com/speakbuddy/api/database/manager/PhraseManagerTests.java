package com.speakbuddy.api.database.manager;

import com.speakbuddy.api.database.repository.PhraseRepository;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

import static com.speakbuddy.api.utility.TestHelper.getObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PhraseManagerTests {

  PhraseEntity mockEntity = getObject("database/manager/phrase_manager/phrase_entity.json", PhraseEntity.class);
  @Mock
  private PhraseRepository phraseRepository;
  @Mock
  private LocalFileProcessor localFileProcessor;
  @InjectMocks
  private PhraseManager phraseManager;

  PhraseManagerTests() throws IOException {
  }

  @Test
  void whenUpsert_returnSuccess() {
    final var ids = new PhraseIds("user-id-01", "phrase-id-01");
    when(phraseRepository.findById(any())).thenReturn(Optional.empty());
    when(phraseRepository.save(any())).thenReturn(mockEntity);

    var actual = phraseManager.upsert(ids, "/root-test/audio/user-id-01/phrase-id-01/19000000.wav");

    assertEquals(mockEntity, actual);

    verify(phraseRepository).findById(ids);
    verify(phraseRepository).save(mockEntity);
  }

  @Test
  void whenUpsert_exists_returnSuccess() {
    final var ids = new PhraseIds("user-id-01", "phrase-id-01");
    when(phraseRepository.findById(any())).thenReturn(Optional.of(mockEntity));
    when(phraseRepository.save(any())).thenReturn(mockEntity);

    var actual = phraseManager.upsert(ids, "/root-test/audio/user-id-01/phrase-id-01/19000000.wav");

    assertEquals(mockEntity, actual);

    verify(phraseRepository).findById(ids);
    verify(localFileProcessor).deleteFile(mockEntity.getFilePath());
    verify(phraseRepository).save(mockEntity);
  }

  @Test
  void whenUpsert_exists_deleteFail_dataNotUpdated() {
    final var ids = new PhraseIds("user-id-01", "phrase-id-01");
    when(phraseRepository.findById(any())).thenReturn(Optional.of(mockEntity));
    when(phraseRepository.save(any())).thenReturn(mockEntity);
    doThrow(new FileProcessorException("fail delete file")).when(localFileProcessor).deleteFile(any());

    var actual = assertThrows(FileProcessorException.class, () -> phraseManager.upsert(ids, "/root-test/audio/user-id-01/phrase-id-01/19000000.wav"));

    assertEquals("fail delete file", actual.getMessage());

    verify(phraseRepository).findById(ids);
    verify(localFileProcessor).deleteFile(mockEntity.getFilePath());
    verify(phraseRepository, never()).save(any());
  }
}
