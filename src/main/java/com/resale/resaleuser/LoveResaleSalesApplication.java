package com.resale.resaleuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LoveResaleSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoveResaleSalesApplication.class, args);
    }

}


