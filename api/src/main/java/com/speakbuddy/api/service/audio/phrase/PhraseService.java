package com.speakbuddy.api.service.audio.phrase;

import com.speakbuddy.api.controller.v1.response.SimpleResponse;
import com.speakbuddy.api.database.manager.PhraseManager;
import com.speakbuddy.api.database.repository.entity.PhraseEntity;
import com.speakbuddy.api.database.repository.entity.ids.PhraseIds;
import com.speakbuddy.api.exception.BadRequestException;
import com.speakbuddy.api.exception.FileProcessorException;
import com.speakbuddy.api.exception.InternalServerError;
import com.speakbuddy.api.utility.AudioProcessor;
import com.speakbuddy.api.utility.FileUtility;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@Service
public class PhraseService {

  private final PhraseManager phraseManager;
  private final FileUtility fileUtility;
  private final AudioProcessorMapper audioProcessorMapper;

  public PhraseService(PhraseManager phraseManager) {
    this.phraseManager = phraseManager;
    this.fileUtility = new LocalFileProcessor("audio");
    this.audioProcessorMapper = new AudioProcessorMapper();
  }

  // get file wav/aiff, and save as wav
  public ResponseEntity<SimpleResponse> savePhrase(String userId, String phraseId, MultipartFile audioFile) {
    log.info("[save phrase] saving phrase by user-id: {} phrase-id: {}", userId, phraseId);
    if (!isFileTypeAllowed(getExtension(audioFile.getOriginalFilename()))) {
      log.warn("[save phrase] requested filename: {}", audioFile.getOriginalFilename());
      throw new BadRequestException("File type not supported");
    }

    final AudioProcessor audioProcessor = audioProcessorMapper.getAudioProcessor("wav");

    final Instant instant = Instant.now();
    
    final File tempFile = saveTempFile(String.valueOf(instant.toEpochMilli()), audioFile);

    final String targetFileName = String.format("%s.%s", instant.toEpochMilli(), "wav");
    final String destinationPath = constructDirectory(userId, phraseId);

    final File destinationFile;
    try {
      destinationFile = fileUtility.storeFile(destinationPath, targetFileName);
      audioProcessor.convert(new FileOutputStream(destinationFile), tempFile);
    } catch (IOException e) {
      throw new FileProcessorException("File not found");
    }

    phraseManager.upsert(new PhraseIds(userId, phraseId), destinationFile.getPath());

    return ResponseEntity.ok(new SimpleResponse("File successfully saved"));
  }

  public ResponseEntity<StreamingResponseBody> getPhrase(String userId, String phraseId, String audioFormat) {
    if (!isFileTypeAllowed(audioFormat)) {
      throw new BadRequestException("Audio file format not supported yet");
    }

    final PhraseEntity existPhraseEntity = phraseManager.findById(new PhraseIds(userId, phraseId));

    final String fileName = Paths.get(existPhraseEntity.getFilePath()).getFileName().toString();
    final String fileNameWithoutExtension = getBaseName(fileName);

    final File myAudioFile = Paths.get("..", "audio", existPhraseEntity.getFilePath()).toFile();

    AudioProcessor processor = audioProcessorMapper.getAudioProcessor(audioFormat);

    final StreamingResponseBody responseBody = outputStream -> processor.convert(outputStream, myAudioFile);
    
    final String headerValue = String.format("attachment; filename=%s.%s", fileNameWithoutExtension, audioFormat);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .contentType(MediaType.parseMediaType(String.format("audio/%s", audioFormat.toLowerCase())))
        .body(responseBody);
  }

  private void handleStreamingResponse(OutputStream outputStream, File file) {
    try (AudioInputStream wavStream = AudioSystem.getAudioInputStream(file)) {
      wavStream.reset();
      AudioSystem.write(wavStream, AudioFileFormat.Type.AIFF, outputStream);
      outputStream.flush();
    } catch (UnsupportedAudioFileException e) {
      log.error("UnsupportedAudioFileException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("Audio file not supported");
    } catch (IOException e) {
      log.error("IOException. file: {}. err: {}", file.getAbsoluteFile(), e.getMessage(), e);
      throw new InternalServerError("IO error");
    }
  }

  public ResponseEntity<StreamingResponseBody> testya(String userId, String phraseId, String audioFormat) {
    //final PhraseEntity existPhraseEntity = phraseManager.findById(new PhraseIds(userId, phraseId));

    //final String fileName = Paths.get(existPhraseEntity.getFilePath()).getFileName().toString();
    //final String fileNameWithoutExtension = getBaseName(fileName);

    // ....
    //final File myAudioFile = Paths.get("..", "audio", existPhraseEntity.getFilePath()).toFile();
    //final File myAudioFile = new File("/Users/wicaksno/code/speakbuddy/wisnu/audio/" + existPhraseEntity.getFilePath());
    final File myAudioFile = new File("/Users/wicaksno/code/speakbuddy/wisnu/audio/9ba2b8a1-ad74-41fc-8d75-ad97eca89f41/testid/1732805255893.wav");

    try (AudioInputStream wavStream = AudioSystem.getAudioInputStream(myAudioFile)) {
      AudioFileFormat inFileFormat = AudioSystem.getAudioFileFormat(myAudioFile);
      if (inFileFormat.getType() != AudioFileFormat.Type.AIFF) {
        System.out.println("Not AIFF");
      }
      //wavStream.reset();

      //if(AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AIFF, wavStream)){
      System.out.println("DO CONVERT!");
      //AudioSystem.write(wavStream, AudioFileFormat.Type.AIFF, outFile);
      File outFile = new File("/Users/wicaksno/code/speakbuddy/wisnu/audio/9ba2b8a1-ad74-41fc-8d75-ad97eca89f41/testid/1732805255893.aiff");
      AudioSystem.write(wavStream,
          AudioFileFormat.Type.AIFF, outFile);

      final StreamingResponseBody responseBody = outputStream -> {
        AudioSystem.write(wavStream, AudioFileFormat.Type.AIFF, outputStream);
        //try (AudioInputStream aiffStream = AudioSystem.getAudioInputStream(aiffFormat, pcmStream)) {
        //  byte[] buffer = new byte[4096];
        //  int bytesRead;
        //  while ((bytesRead = aiffStream.read(buffer)) != -1) {
        //    outputStream.write(buffer, 0, bytesRead);
        //  }
        //}
      };

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hehehehe.aiff")
          .contentType(MediaType.parseMediaType("audio/wav"))
          .body(responseBody);
      //}
      //
      //AudioFormat targetFormat = new AudioFormat(
      //    AudioFormat.Encoding.PCM_SIGNED,
      //    sourceFormat.getSampleRate(),
      //    16,
      //    sourceFormat.getChannels(),
      //    sourceFormat.getChannels() * 2,
      //    sourceFormat.getSampleRate(),
      //    false
      //);
      //
      //AudioInputStream pcmStream = AudioSystem.getAudioInputStream(targetFormat, wavStream);

      //AudioFormat aiffFormat = new AudioFormat(
      //    AudioFormat.Encoding.PCM_SIGNED,
      //    sourceFormat.getSampleRate(),
      //    sourceFormat.getSampleSizeInBits(),
      //    sourceFormat.getChannels(),
      //    sourceFormat.getFrameSize(),
      //    sourceFormat.getSampleRate(),
      //    sourceFormat.isBigEndian()
      //);

      //StreamingResponseBody responseBody = outputStream -> {
      //  try (AudioInputStream aiffStream = AudioSystem.getAudioInputStream(aiffFormat, pcmStream)) {
      //    byte[] buffer = new byte[4096];
      //    int bytesRead;
      //    while ((bytesRead = aiffStream.read(buffer)) != -1) {
      //      outputStream.write(buffer, 0, bytesRead);
      //    }
      //  }
      //};

      //byte[] buffer = wavStream.readAllBytes();

      //return ResponseEntity.ok()
      //    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hehehehe.wav")
      //    .contentType(MediaType.parseMediaType("audio/wav"))
      //    .body(buffer);

      //try (AudioInputStream aiffStream = AudioSystem.getAudioInputStream(aiffFormat, wavStream)) {
      //  byte[] buffer = wavStream.readAllBytes();
      //
      //  return ResponseEntity.ok()
      //      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.aiff")
      //      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      //      .body(buffer);
      //}


      //return ResponseEntity.ok()
      //    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.aiff")
      //    .contentType(MediaType.APPLICATION_OCTET_STREAM)
      //    .body(responseBody);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }


    // ...

    /**
     try {
     final InputStream inputStream = fileUtility.getFile(existPhraseEntity.getFilePath());
     StreamingResponseBody responseBody = outputStream -> {

     int numberOfBytesToWrite;
     byte[] data = new byte[1024];
     while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
     System.out.println("Writing some bytes..");
     outputStream.write(data, 0, numberOfBytesToWrite);
     }

     inputStream.close();
     };
     return ResponseEntity.ok()
     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.wav")
     .contentType(MediaType.APPLICATION_OCTET_STREAM)
     .body(responseBody);
     } catch (IOException e) {
     e.printStackTrace();  // TODO impl
     }





     //return new ResponseEntity<StreamingResponseBody>
     //    (responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);

     return ResponseEntity.ok()
     //.header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.%s", fileNameWithoutExtension, audioFormat))
     .contentType(MediaType.valueOf("audio/wav"))
     .body(os -> {
     try (InputStream inputStream = fileUtility.getFile(existPhraseEntity.getFilePath())) {
     inputStream.transferTo(os);
     } catch (IOException e) {
     log.error("[getFile] fail read file with path: {}", "filePath", e);
     throw new FileProcessorException("File read failed");
     }
     });
     **/
  }

  private String constructDirectory(String userId, String phraseId) {
    return String.format("%s/%s", userId, phraseId);
  }

  private boolean isFileTypeAllowed(String fileName) {
    return List.of("wav", "aiff").contains(fileName);
  }

  private File saveTempFile(String fileName, MultipartFile audioFile) {
    try {
      final var tempFilename = String.format("%s.%s", fileName, FilenameUtils.getExtension(audioFile.getOriginalFilename()));
      final File tempFile = new File("/Users/wicaksno/code/speakbuddy/wisnu/temp/" + tempFilename);
      audioFile.transferTo(tempFile);
      return tempFile;
    } catch (IOException e) {
      throw new FileProcessorException("Error save temporary file");
    }
  }

}
