package com.legal.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.legal.platform.mapper")
public class LegalPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegalPlatformApplication.class, args);
    }
}
