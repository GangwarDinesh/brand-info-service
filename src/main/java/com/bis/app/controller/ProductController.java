package com.bis.app.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bis.app.entity.Product;
import com.bis.app.service.ProductService;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(value = "*")
public class ProductController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	private ProductService productService;
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> search(@RequestParam("inputText") String inputText){
		
		logger.debug("[Search method ]"+inputText);
		
		List<Map<String, Object>> responseList = productService.getProducts(inputText);
		responseList = responseList.stream().sorted(Comparator.comparing(o-> String.valueOf(o.get("id")))).collect(Collectors.toList());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		responseMap.put("response", responseList);
		if(null != responseList && !responseList.isEmpty()) {
			responseMap.put("status", HttpStatus.FOUND);
		}else {
			responseMap.put("status", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	@GetMapping(value = "/createAllIndexing", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> prepareIndexing(){	
		logger.debug("[createAllIndexing method ]");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		
		Map<String, Integer> result = productService.prepareIndexing();
		if(null != result && !result.isEmpty()) {
			responseMap.put("status", HttpStatus.OK);
			responseMap.put("message", "All indexing have been done.");
			responseMap.put("reponse", result);
		}else {
			responseMap.put("status", HttpStatus.EXPECTATION_FAILED);
			responseMap.put("message", "Technical error occurred.");
			responseMap.put("reponse", "{}");
		}
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addProduct(@RequestBody List<Product> productsList){
		
		logger.debug("[add method ]");
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		
		Map<String, Integer> result = productService.addProduct(productsList);
		if(null != result && !result.isEmpty()) {
			responseMap.put("status", HttpStatus.OK);
			responseMap.put("message", "Product created and indexed successfully.");
			responseMap.put("reponse", result);
		}else {
			responseMap.put("status", HttpStatus.EXPECTATION_FAILED);
			responseMap.put("message", "Technical error occurred.");
			responseMap.put("reponse", "{}");
		}
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateProduct(@RequestBody Product product){
		
		logger.debug("[update method ]");
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		
		ShardInfo result = productService.updateProduct(product);
		if(null != result) {
			responseMap.put("status", HttpStatus.OK);
			responseMap.put("message", "Product details updated and re-indexed successfully.");
			responseMap.put("reponse", result);
		}else {
			responseMap.put("status", HttpStatus.EXPECTATION_FAILED);
			responseMap.put("message", "Technical error occurred.");
			responseMap.put("reponse", "{}");
		}
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> removeProduct(@RequestParam("id") String id){
		
		logger.debug("[remove method ]");
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		
		ShardInfo result = productService.removeProduct(id);
		if(null != result) {
			responseMap.put("status", HttpStatus.OK);
			responseMap.put("message", "Product deleted and index removed successfully.");
			responseMap.put("reponse", result);
		}else {
			responseMap.put("status", HttpStatus.EXPECTATION_FAILED);
			responseMap.put("message", "Technical error occurred.");
			responseMap.put("reponse", "{}");
		}
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/searchIndexedResult", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> searchIndexedResult(){
		
		logger.debug("[searchIndexedResult method ]");
		
		List<Map<String, Object>> responseList = productService.getProducts(null);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		responseMap.put("response", responseList);
		if(null != responseList && !responseList.isEmpty()) {
			responseMap.put("status", HttpStatus.FOUND);
		}else {
			responseMap.put("status", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}

}
