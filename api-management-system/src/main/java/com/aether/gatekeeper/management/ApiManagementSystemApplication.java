package com.aether.gatekeeper.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class ApiManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiManagementSystemApplication.class, args);
	}

}
