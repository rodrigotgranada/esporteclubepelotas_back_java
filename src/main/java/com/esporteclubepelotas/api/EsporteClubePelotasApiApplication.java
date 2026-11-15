package com.esporteclubepelotas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync(order = Ordered.HIGHEST_PRECEDENCE)
@EnableRetry
public class EsporteClubePelotasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsporteClubePelotasApiApplication.class, args);
	}

}
