package com.bis.app.service;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;

import com.bis.app.entity.Product;

public interface ProductService {

	IndexResponse createElasticIndex(Product product);
	
	List<Map<String, Object>> getProducts(Map<String, String> requestMap);
	
	Map<String, Integer> prepareIndexing();
	
	Map<String, Integer> addProduct(List<Product> productsList);
	
	ShardInfo updateProduct(Product product);
	
	ShardInfo removeProduct(String id);
}
