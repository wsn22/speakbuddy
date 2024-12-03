package com.speakbuddy.api.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileUtility {
  File storeFile(String targetPath, String targetFileName);
  InputStream getFile(String filePath) throws IOException;
  void deleteFile(String filePath);
}
