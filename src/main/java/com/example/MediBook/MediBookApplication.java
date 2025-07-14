package com.example.MediBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
@EnableScheduling
@EnableTransactionManagement
@ComponentScan(basePackages = "com.example.MediBook")
public class MediBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediBookApplication.class, args);
	}

}
