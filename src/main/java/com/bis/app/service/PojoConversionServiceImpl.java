package com.bis.app.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bis.app.util.CommonUtil;


@Service
public class PojoConversionServiceImpl implements PojoConversionService{

	@Value("${pojo.file.base.path}")
	private String fileBasePath;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Override
	public List<File> readAllFiles(String input, String uniqueFolder) throws IOException {
	    
		StringBuilder builder = new StringBuilder("");
		builder.append(fileBasePath);
		builder.append("/");
		builder.append(uniqueFolder);
		
	    File newDir = new File(fileBasePath, uniqueFolder);
	    newDir.mkdir();
	    
	    Path path = Files.write(Paths.get(builder.toString()+"/input.json"), input.getBytes());
	    URL inputURI = path.toFile().toURI().toURL();
	    String packageName = "com.pojo";  
        File outputPojoDirectory = new File(builder.toString());  
        outputPojoDirectory.mkdirs();  
        try {  
             commonUtil.convert2JSON(inputURI, outputPojoDirectory, packageName, "Parent");  
        } catch (IOException e) {  
        	e.printStackTrace();
        }  

	    List<File> files = null;
	    try (Stream<Path> walk = Files.walk(Paths.get(builder.toString()))) {

	    	files = walk.filter(f-> !f.toString().endsWith(".json") && f.toFile().isFile())
					.map(Path::toFile).collect(Collectors.toList());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		return files;
	}

	@Override
	public void removeDirectory(String uniqueFolder) {
		StringBuilder builder = new StringBuilder("");
		builder.append(fileBasePath);
		builder.append("/");
		builder.append(uniqueFolder);
		try(Stream<Path> walk = Files.walk(Paths.get(builder.toString()))){
	    	walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	    }catch (Exception e) {
	    	e.printStackTrace();
		}
	}

}
