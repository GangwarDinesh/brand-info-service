package com.bis.app.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PojoConversionService {
	
	List<File> readAllFiles(String input, String uniqueFolder) throws IOException;
	
	void removeDirectory(String uniqueFolder);
}
