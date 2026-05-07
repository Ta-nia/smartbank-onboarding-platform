package com.smartbank.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

//All other services will call this server on startup to fetch their properties.

@SpringBootApplication
@EnableConfigServer
public class ConfigServerAplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerAplication.class, args);
	}
}
