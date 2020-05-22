package com.bis.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bis.app.entity.Product;
import com.bis.app.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private Client client;
	
	@Autowired
	private ProductRepository productRepository;

	@SuppressWarnings("unchecked")
	@Override
	public IndexResponse createElasticIndex(Product product) {
		IndexResponse indexResponse = null;

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> userObjectMap = mapper.convertValue(product, Map.class);

		IndexRequestBuilder builder[] = new IndexRequestBuilder[] {
				client.prepareIndex("products", "product", String.valueOf(product.getId())).setSource(userObjectMap) };
		indexResponse = builder[0].get();

		return indexResponse;
	}
	
	@Override
	public List<Map<String, Object>> getProducts(String inputText){
		List<Map<String, Object>> response = new ArrayList<>();
		SearchResponse searchResponse = null;
		try {
			if(null != inputText && !inputText.isEmpty()) {
				searchResponse = client.prepareSearch("products")
						.setTypes("product")
						.setSearchType(SearchType.QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.wildcardQuery("brandName", "*"+inputText.toLowerCase()+"*"))
						.setSize(10000)
						.get();
			}else {
				searchResponse = client.prepareSearch("products")
						.setTypes("product")
						.setQuery(QueryBuilders.matchAllQuery())
						.setSize(10000)
						.get();
				
			}
			if( null != searchResponse) {
				List<SearchHit> hitsList = Arrays.asList(searchResponse.getHits().getHits());
				hitsList.forEach(hits->{
					response.add(hits.getSourceAsMap());
				});
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> prepareIndexing(){
		
		List<Product> productList = productRepository.findAll();

		ObjectMapper mapper = new ObjectMapper();
		List<IndexRequestBuilder> indexRequestBuilders = new ArrayList<>();
		Map<String, Integer> result = new HashMap<>();
		int successCount = 0;
		int failedCount = 0;
		for(int count = 0; count< productList.size(); count++) {
			Product product = productList.get(count);
			Map<String, Object> userObjectMap = mapper.convertValue(product, Map.class);
			try {
				indexRequestBuilders.add(client.prepareIndex("products", "product", String.valueOf(product.getId())).setSource(userObjectMap));
				successCount = successCount +indexRequestBuilders.get(count).get().getShardInfo().getSuccessful(); 
				failedCount = failedCount + indexRequestBuilders.get(count).get().getShardInfo().getFailed();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.put("total_count", successCount+failedCount);
		result.put("success_count", successCount);
		result.put("failed_count", failedCount);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> addProduct(List<Product> productsList) {
		int successCount = 0;
		int failedCount = 0;
		
		productsList = productRepository.insert(productsList);
		
		ObjectMapper mapper = new ObjectMapper();
		List<IndexRequestBuilder> indexRequestBuilders = new ArrayList<>();
		Map<String, Integer> result = new HashMap<>();
		
		for(int count = 0; count< productsList.size(); count++) {
			Product product = productsList.get(count);
			Map<String, Object> productMap = mapper.convertValue(product, Map.class);
			try {
				indexRequestBuilders.add(client.prepareIndex("products", "product", String.valueOf(product.getId())).setSource(productMap));
				successCount = successCount +indexRequestBuilders.get(count).get().getShardInfo().getSuccessful(); 
				failedCount = failedCount + indexRequestBuilders.get(count).get().getShardInfo().getFailed();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		result.put("total_count", successCount+failedCount);
		result.put("success_count", successCount);
		result.put("failed_count", failedCount);
		
		return result;
	}

	@Override
	public ShardInfo updateProduct(Product product) {
		Product updatedProduct = productRepository.save(product);
		ShardInfo shardInfo = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index("products")
							.type("product")
								.id(String.valueOf(updatedProduct.getId()))
									.doc(mapper.convertValue(updatedProduct, Map.class));
			ActionFuture<UpdateResponse> actionFuture = client.update(updateRequest);
			shardInfo = actionFuture.actionGet().getShardInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shardInfo;
	}

	@Override
	public ShardInfo removeProduct(String id) {
		productRepository.deleteById(Long.valueOf(id));
		ShardInfo shardInfo = null;
		try {
			DeleteResponse deleteResponse = client.prepareDelete("products", "product", id).get();
			shardInfo = deleteResponse.getShardInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return shardInfo;
	}
}
