package com.example.hypeadvice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Classe principal da aplicacao Hype Advice.
 *
 * <p>Estende {@link SpringBootServletInitializer} porque o projeto eh empacotado
 * como WAR para ser executado em containers servlet externos quando necessario,
 * mas tambem inicia normalmente via {@code spring-boot:run}.
 *
 * <p>Define um {@code MessageSource} reutilizavel para mensagens i18n
 * carregadas de {@code classpath:messages/msg}.
 */
@SpringBootApplication
public class HypeadviceApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(HypeadviceApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(HypeadviceApplication.class, args);
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/msg");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}
	
}
