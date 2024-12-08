package com.speakbuddy.api.database.manager;

import com.speakbuddy.api.database.repository.PhraseRepository;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.EntityNotFoundException;
import com.speakbuddy.api.utility.FileUtility;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PhraseManager {
  private final PhraseRepository phraseRepository;
  private final FileUtility fileUtility;

  public PhraseManager(PhraseRepository phraseRepository, LocalFileProcessor localFileProcessor) {
    this.phraseRepository = phraseRepository;
    this.fileUtility = localFileProcessor;
  }

  public PhraseEntity findById(PhraseIds id) {
    Optional<PhraseEntity> phraseEntity = phraseRepository.findById(id);

    if (phraseEntity.isEmpty()) {
      throw new EntityNotFoundException("Phrase not found");
    }

    return phraseEntity.get();
  }

  @Transactional
  public PhraseEntity upsert(PhraseIds id, String path) {
    final Optional<PhraseEntity> existData = phraseRepository.findById(id);

    if (existData.isEmpty()) {
      return phraseRepository.save(PhraseEntity.builder()
          .phraseId(id.getPhraseId())
          .userId(id.getUserId())
          .filePath(path)
          .build());
    }

    fileUtility.deleteFile(existData.get().getFilePath());

    final PhraseEntity updatedEntity = existData.get();
    updatedEntity.setFilePath(path);

    return phraseRepository.save(updatedEntity);
  }
}
