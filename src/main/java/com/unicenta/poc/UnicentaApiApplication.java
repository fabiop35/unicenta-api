package com.unicenta.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class UnicentaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnicentaApiApplication.class, args);
	}

}
