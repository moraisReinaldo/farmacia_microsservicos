package com.farmacia.produto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.farmacia.produto", "com.farmacia.common"})
@EnableDiscoveryClient
public class ProdutoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProdutoServiceApplication.class, args);
    }
}
