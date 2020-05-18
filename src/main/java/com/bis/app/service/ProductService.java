package com.bis.app.service;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;

import com.bis.app.entity.Product;

public interface ProductService {

	IndexResponse createElasticIndex(Product product);
	
	List<Map<String, Object>> getProducts(String inputText);
	
	Map<String, Integer> prepareIndexing();
}
