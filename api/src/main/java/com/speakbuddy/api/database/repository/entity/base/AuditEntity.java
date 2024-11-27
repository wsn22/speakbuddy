package com.speakbuddy.api.database.repository.entity.base;


import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
@Data
public class AuditEntity {
  @CreatedDate
  private Instant createdAt;
  @LastModifiedDate
  private Instant lastModifiedAt;
}
