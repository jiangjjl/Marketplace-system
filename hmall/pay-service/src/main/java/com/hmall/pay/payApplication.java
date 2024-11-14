package com.hmall.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.hmall.api.client")
@MapperScan("com.hmall.pay.mapper")
@SpringBootApplication
public class payApplication {
    public static void main(String[] args) {
        SpringApplication.run(payApplication.class, args);
    }
}