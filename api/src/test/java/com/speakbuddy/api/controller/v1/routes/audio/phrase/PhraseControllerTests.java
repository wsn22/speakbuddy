package com.speakbuddy.api.controller.v1.routes.audio.phrase;

import com.speakbuddy.api.service.audio.phrase.PhraseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PhraseController.class)
class PhraseControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PhraseService phraseService;


  @Test
  void testGetPhrase() throws Exception {
    when(phraseService.getPhrase(any(), any(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

    mockMvc.perform(MockMvcRequestBuilders
            .get("/audio/user/user-id-test/phrase/phrase-id-test/aiff"))
        .andExpectAll(
            status().isOk()
        );

    verify(phraseService).getPhrase("user-id-test", "phrase-id-test", "aiff");
  }
}
