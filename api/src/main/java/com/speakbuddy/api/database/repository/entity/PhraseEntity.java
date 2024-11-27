package com.speakbuddy.api.database.repository.entity;

import com.speakbuddy.api.database.repository.entity.base.AuditEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@IdClass(value = PhraseIds.class)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "dictionary", name = "phrases")
public class PhraseEntity extends AuditEntity {
  @Id
  private String userId;
  @Id
  private String phraseId;
  private String filePath;
}
