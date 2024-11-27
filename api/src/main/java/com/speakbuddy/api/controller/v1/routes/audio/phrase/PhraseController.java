package com.speakbuddy.api.controller.v1.routes.audio.phrase;

import com.speakbuddy.api.controller.v1.response.SimpleResponse;
import com.speakbuddy.api.service.audio.phrase.PhraseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/audio/user/{userId}/phrase")
public class PhraseController {

  private final PhraseService phraseService;

  public PhraseController(PhraseService phraseService) {
    this.phraseService = phraseService;
  }

  @GetMapping("/{phraseId}/{audioFormat}")
  public ResponseEntity<StreamingResponseBody> getPhrase(@PathVariable("userId") String userId, @PathVariable("phraseId") String phraseId, @PathVariable("audioFormat") String audioFormat) {
    return phraseService.getPhrase(userId, phraseId, audioFormat);
  }

  @PostMapping("/{phraseId}")
  public ResponseEntity<SimpleResponse> insertPhrase(@PathVariable("userId") String userId, @PathVariable("phraseId") String phraseId, @RequestParam("file") MultipartFile audioFile) {
    return phraseService.savePhrase(userId, phraseId, audioFile);
  }
}
