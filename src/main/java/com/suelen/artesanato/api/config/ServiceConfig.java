package com.suelen.artesanato.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.suelen.artesanato.api.storage.local.FotoStorage;
import com.suelen.artesanato.api.storage.local.FotoStorageLocal;

@Configuration
public class ServiceConfig {

	@Bean
	public FotoStorage fotoStorage() {
		return new FotoStorageLocal();
	}
	
}
