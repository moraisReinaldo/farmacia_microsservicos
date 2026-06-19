package com.farmacia.notafiscal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.farmacia.notafiscal", "com.farmacia.common"})
@EnableDiscoveryClient
public class NotaFiscalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotaFiscalServiceApplication.class, args);
    }
}
