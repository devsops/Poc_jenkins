package com.bosch.pai.retail.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class UserAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAnalyticsApplication.class, args);
    }

}