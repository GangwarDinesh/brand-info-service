package com.bis.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bis.app.entity.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {

}
