package com.bibliotheque.bibliotheque;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.bibliotheque.bibliotheque", "controller", "service", "DAO", "config",
		"AlertUtil" })
@EntityScan(basePackages = "entity")
@EnableJpaRepositories(basePackages = "DAO")
public class BibliothequeApplication {

	public static void main(String[] args) {
		javafx.application.Application.launch(JavaFxApplication.class, args);
	}

}
