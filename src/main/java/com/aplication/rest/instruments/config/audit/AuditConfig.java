package com.aplication.rest.instruments.config.audit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Habilita el auditor
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    // Clase interna para definir "Quién" está modificando los datos
    public static class AuditorAwareImpl implements AuditorAware<String>{
        @Override
        public Optional<String> getCurrentAuditor() {
            // AHORA: Como no tenemos seguridad, devolvemos un valor fijo system_user
            // CUANDO TENGA SECURITY: leer SecurityContextHolder.getContext().getAuthentication().getName()
            return Optional.of("system_user");
        }
    }
}
