package com.speakbuddy.api.database.repository;

import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhraseRepository extends JpaRepository<PhraseEntity, PhraseIds> {
}
