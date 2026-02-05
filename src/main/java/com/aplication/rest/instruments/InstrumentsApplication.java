package com.aplication.rest.instruments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InstrumentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstrumentsApplication.class, args);
	}

}
