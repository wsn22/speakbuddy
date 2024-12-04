package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.controller.v1.response.SimpleResponse;
import com.speakbuddy.api.database.manager.PhraseManager;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.utility.AudioProcessor;
import com.speakbuddy.api.utility.audio_processor.AudioProcessorMapper;
import com.speakbuddy.api.utility.impl.LocalFileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@Service
public class PhraseService extends BasePhraseService {

  private final PhraseManager phraseManager;

  public PhraseService(PhraseManager phraseManager) {
    this.phraseManager = phraseManager;
  }

  // get file wav/aiff, and save as wav
  public ResponseEntity<SimpleResponse> savePhrase(String userId, String phraseId, MultipartFile audioFile) {
    log.info("[save phrase] saving phrase by user-id: {} phrase-id: {}", userId, phraseId);
    validateSupportedFileType(getExtension(audioFile.getOriginalFilename()));

    final Instant currentTime = Instant.now();

    // TODO: All epoch millis can be replaced with formatted datetime to increase readability
    final File tempFile = saveTempFile(
        String.format("%s.%s", currentTime.toEpochMilli(), FilenameUtils.getExtension(audioFile.getOriginalFilename())), audioFile);

    final File savedFile = saveFile(tempFile, String.format("%s.%s", currentTime.toEpochMilli(), "wav"), userId, phraseId);

    phraseManager.upsert(new PhraseIds(userId, phraseId), savedFile.getPath());

    return ResponseEntity.ok(new SimpleResponse("File successfully saved"));
  }

  public ResponseEntity<StreamingResponseBody> getPhrase(String userId, String phraseId, String audioFormat) {
    log.info("[get phrase] get phrase by user-id: {} phrase-id: {} audio-format: {}", userId, phraseId, audioFormat);

    validateSupportedFileType(audioFormat);

    final PhraseEntity existPhraseEntity = phraseManager.findById(new PhraseIds(userId, phraseId));

    final String fileName = Paths.get(existPhraseEntity.getFilePath()).getFileName().toString();
    final String fileNameWithoutExtension = getBaseName(fileName);

    final File myAudioFile = new LocalFileProcessor("audio").getFile(existPhraseEntity.getFilePath());

    final AudioProcessor processor = new AudioProcessorMapper().getAudioProcessor(audioFormat);

    final StreamingResponseBody responseBody = outputStream -> processor.convert(myAudioFile, outputStream);

    final String headerValue = String.format("attachment; filename=%s.%s", fileNameWithoutExtension, audioFormat);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .contentType(MediaType.parseMediaType(String.format("audio/%s", audioFormat.toLowerCase())))
        .body(responseBody);
  }

}
