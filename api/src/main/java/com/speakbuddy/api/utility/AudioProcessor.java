package com.speakbuddy.api.utility;

import java.io.File;
import java.io.OutputStream;

public interface AudioProcessor {
  void convert(File inputFile, OutputStream outputStream);

  void convert(File inputFile, File outputFile);
}
