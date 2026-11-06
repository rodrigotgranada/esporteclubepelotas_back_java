package com.esporteclubepelotas.api.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("Executando Flyway repair preventivo para sincronizar checksums do histórico...");
            flyway.repair();
            log.info("Executando Flyway migrate...");
            flyway.migrate();
        };
    }
}
