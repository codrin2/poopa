package com.ssafy.foofa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FoofaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoofaApplication.class, args);
    }

}

