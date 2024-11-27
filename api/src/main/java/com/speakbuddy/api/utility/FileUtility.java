package com.speakbuddy.api.utility;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileUtility {
  String storeFile(MultipartFile file, String targetPath, String targetFileName);
  InputStream getFile(String filePath) throws IOException;
}
