package com.esporteclubepelotas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EsporteClubePelotasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsporteClubePelotasApiApplication.class, args);
	}

}
