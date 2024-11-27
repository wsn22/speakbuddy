package com.speakbuddy.api.database.repository.entity.ids;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhraseIds implements Serializable {
  private String userId;
  private String phraseId;
}
