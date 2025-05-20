package com.example.lineta_posts_interaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LinetaPostsInteractionApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinetaPostsInteractionApplication.class, args);
	}

}
