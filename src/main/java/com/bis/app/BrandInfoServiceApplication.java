package com.bis.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class BrandInfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrandInfoServiceApplication.class, args);
	}

}
