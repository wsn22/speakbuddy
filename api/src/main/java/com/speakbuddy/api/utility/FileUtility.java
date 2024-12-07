package com.speakbuddy.api.utility;

import java.io.File;

public interface FileUtility {
  File storeFile(String targetPath, String targetFileName);

  File getFile(String filePath);

  void deleteFile(String filePath);
}
