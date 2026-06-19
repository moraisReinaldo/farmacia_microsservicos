package com.farmacia.estoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.farmacia.estoque", "com.farmacia.common"})
@EnableDiscoveryClient
public class EstoqueServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstoqueServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
