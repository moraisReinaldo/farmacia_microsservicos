package com.farmacia.receita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.farmacia.receita", "com.farmacia.common"})
@EnableDiscoveryClient
public class ReceitaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReceitaServiceApplication.class, args);
    }
}
