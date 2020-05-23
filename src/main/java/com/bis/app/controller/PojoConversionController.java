package com.bis.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bis.app.service.PojoConversionService;

@RestController
@RequestMapping("/api/v1/pojo")
@CrossOrigin(value = "*")
public class PojoConversionController {
	
	@Autowired
	private PojoConversionService pojoConversionService;

	@PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
	public void generatePojo(HttpServletResponse response, @RequestBody String requestJSON) throws IOException {
		String uniqueFolder = String.valueOf(System.currentTimeMillis());
		response.setStatus(HttpServletResponse.SC_OK);
	    response.addHeader("Content-Disposition", "attachment; filename=\"JsonPojo.zip\"");
	    ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
	    
	    List<File> files = pojoConversionService.readAllFiles(requestJSON, uniqueFolder);
	    
	    for (File file : files) {
	        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
	        FileInputStream fileInputStream = new FileInputStream(file);

	        IOUtils.copy(fileInputStream, zipOutputStream);

	        fileInputStream.close();
	        zipOutputStream.closeEntry();
	    }    
	    zipOutputStream.close();
	    
	    pojoConversionService.removeDirectory(uniqueFolder);
	    
	}
}
