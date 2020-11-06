package com.whrailway.sgsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class SgspApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgspApplication.class, args);
    }

}
