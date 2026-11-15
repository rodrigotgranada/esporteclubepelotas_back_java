package com.esporteclubepelotas.api.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Slf4j
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(Environment environment) {
        boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));
        return flyway -> {
            if (!isProd) {
                log.info("Executando Flyway repair preventivo para sincronizar checksums do histórico...");
                flyway.repair();
            } else {
                log.info("Perfil 'prod' ativo: executando estritamente Flyway migrate() (repair desativado).");
            }
            flyway.migrate();
        };
    }
}
