package com.speakbuddy.api.utility.audio_processor;

import com.speakbuddy.api.utility.AudioProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper class to get corresponding audio processor based on the file extension.
 */
public class AudioProcessorMapper {
  final Map<String, AudioProcessor> audioProcessorMap = new HashMap<>();

  public AudioProcessorMapper() {
    audioProcessorMap.put("wav", new WavConverter());
    audioProcessorMap.put("aiff", new AiffConverter());
  }

  public AudioProcessor getAudioProcessor(String audioProcessor) {
    return audioProcessorMap.get(audioProcessor);
  }
}
