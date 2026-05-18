package com.crm.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.crm.demo.mapper")
public class CrmDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmDemoApplication.class, args);
    }
}
