package com.suelen.artesanato.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import com.suelen.artesanato.api.config.property.ArtesanatoApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(ArtesanatoApiProperty.class)
public class ArtesanatoApiApplication {
	
	private static ApplicationContext APPLICATION_CONTEXT;

	public static void main(String[] args) {
		SpringApplication.run(ArtesanatoApiApplication.class, args);
	}

}
