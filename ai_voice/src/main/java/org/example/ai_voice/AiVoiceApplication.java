package org.example.ai_voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AiVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiVoiceApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")  // Remplace "*" par ton URL frontend si tu veux restreindre
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}



