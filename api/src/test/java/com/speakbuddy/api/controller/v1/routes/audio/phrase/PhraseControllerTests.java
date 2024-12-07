package com.speakbuddy.api.controller.v1.routes.audio.phrase;

import com.speakbuddy.api.service.audio.phrase.PhraseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

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
    final File mockFile = ResourceUtils.getFile("classpath:controller/v1/routes/audio/phrase/success.wav");
    final StreamingResponseBody mockResponse = outputStream -> Files.copy(mockFile.toPath(), outputStream);

    when(phraseService.getPhrase(any(), any(), any())).thenReturn(ResponseEntity.ok(mockResponse));

    final var actual = mockMvc.perform(MockMvcRequestBuilders
            .get("/audio/user/user-id-test/phrase/phrase-id-test/wav"))
        .andExpectAll(
            status().isOk()
        );

    // should make sure the file is same
    //assertArrayEquals(Files.readAllBytes(mockFile.toPath()), actual.andReturn().getResponse().getContentAsByteArray());
    verify(phraseService).getPhrase("user-id-test", "phrase-id-test", "wav");
  }

  @Test
  void testPostPhrase() throws Exception {
    when(phraseService.getPhrase(any(), any(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

    final File mockFile = ResourceUtils.getFile("classpath:controller/v1/routes/audio/phrase/success.wav");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "filename.txt", "audio/wav", new FileInputStream(mockFile));

    mockMvc.perform(MockMvcRequestBuilders
            .multipart("/audio/user/user-id-test/phrase/phrase-id-test")
            .file(mockMultipartFile))
        .andExpectAll(
            status().isOk()
        );

    verify(phraseService).savePhrase("user-id-test", "phrase-id-test", mockMultipartFile);
  }

}
