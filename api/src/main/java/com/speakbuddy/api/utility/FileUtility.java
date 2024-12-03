package com.speakbuddy.api.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileUtility {
  String storeFile(File file, String targetPath, String targetFileName);
  InputStream getFile(String filePath) throws IOException;
}
