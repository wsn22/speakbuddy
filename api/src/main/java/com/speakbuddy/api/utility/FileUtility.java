package com.speakbuddy.api.utility;

import java.io.File;
import java.io.IOException;

public interface FileUtility {
  File storeFile(String targetPath, String targetFileName);

  File getFile(String filePath) throws IOException;
  void deleteFile(String filePath);
}
