package com.speakbuddy.api.database.manager;

import com.speakbuddy.api.database.repository.PhraseRepository;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PhraseManager {
  private final PhraseRepository phraseRepository;

  public PhraseManager(PhraseRepository phraseRepository) {
    this.phraseRepository = phraseRepository;
  }

  public PhraseEntity findById(PhraseIds id) {
    Optional<PhraseEntity> phraseEntity = phraseRepository.findById(id);

    if (phraseEntity.isEmpty()) {
      throw new EntityNotFoundException("Phrase not found");
    }

    return phraseEntity.get();
  }

  public PhraseEntity upsert(PhraseIds id, String path) {
    final Optional<PhraseEntity> existData = phraseRepository.findById(id);

    if (existData.isEmpty()) {
      return phraseRepository.save(PhraseEntity.builder()
          .phraseId(id.getPhraseId())
          .userId(id.getUserId())
          .filePath(path)
          .build());
    }

    final PhraseEntity updatedEntity = existData.get();
    updatedEntity.setFilePath(path);

    return phraseRepository.save(updatedEntity);
  }
}
