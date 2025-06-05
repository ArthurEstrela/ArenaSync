package com.ajs.arenasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // Importe esta anotação

@SpringBootApplication
@EnableCaching // Adicione esta anotação para habilitar o caching
public class ArenasyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArenasyncApplication.class, args);
	}

}