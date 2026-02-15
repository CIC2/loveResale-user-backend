package com.resale.homeflyuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TmgVsoSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmgVsoSalesApplication.class, args);
    }

}


