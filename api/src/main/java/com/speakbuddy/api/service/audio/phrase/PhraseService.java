package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.controller.v1.response.SimpleResponse;
import com.speakbuddy.api.database.manager.PhraseManager;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.utility.FileUtility;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@Service
public class PhraseService {

  private final PhraseManager phraseManager;
  private final FileUtility fileUtility;

  public PhraseService(PhraseManager phraseManager) {
    this.phraseManager = phraseManager;
    this.fileUtility = new LocalFileProcessor("audio");
  }


  public ResponseEntity<SimpleResponse> savePhrase(String userId, String phraseId, MultipartFile audioFile) {
    log.info("[save phrase] saving phrase by user-id: {} phrase-id: {}", userId, phraseId);
    if (!isFileTypeAllowed(audioFile.getOriginalFilename())) {
      log.warn("[save phrase] requested filename: {}", audioFile.getOriginalFilename());
      throw new BadRequestException("File type not supported");
    }

    final var path = constructDirectory(userId, phraseId);
    final var fileName = String.format("%s.%s", Instant.now().toEpochMilli(), FilenameUtils.getExtension(audioFile.getOriginalFilename()));

    // assume that user already authenticated and rate limiter is
    final String destinationPath = fileUtility.storeFile(audioFile, path, fileName);

    //TODO: transform audio file https://docs.oracle.com/javase/tutorial/sound/converters.html


    // do upsert to database
    phraseManager.upsert(new PhraseIds(userId, phraseId), destinationPath);

    return ResponseEntity.ok(new SimpleResponse("File successfully saved"));
  }

  public ResponseEntity<StreamingResponseBody> getPhrase(String userId, String phraseId, String audioFormat) {
    final PhraseEntity existPhraseEntity = phraseManager.findById(new PhraseIds(userId, phraseId));

    final String fileName = Paths.get(existPhraseEntity.getFilePath()).getFileName().toString();
    final String fileNameWithoutExtension = getBaseName(fileName);

    return ResponseEntity.ok()
        //.header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.%s", fileNameWithoutExtension, audioFormat))
        //.contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(os -> {
          try (InputStream inputStream = fileUtility.getFile(existPhraseEntity.getFilePath())) {
            inputStream.transferTo(os);
          } catch (IOException e) {
            log.error("[getFile] fail read file with path: {}", "filePath", e);
            throw new FileProcessorException("File read failed");
          }
        });
  }

  private String constructDirectory(String userId, String phraseId) {
    return String.format("%s/%s", userId, phraseId);
  }

  private boolean isFileTypeAllowed(String fileName) {
    return List.of("wav", "m4a").contains(getExtension(fileName));
  }

}
