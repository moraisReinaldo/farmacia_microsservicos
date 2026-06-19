package com.farmacia.relatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.farmacia.relatorio", "com.farmacia.common"})
@EnableDiscoveryClient
public class RelatorioServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RelatorioServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
