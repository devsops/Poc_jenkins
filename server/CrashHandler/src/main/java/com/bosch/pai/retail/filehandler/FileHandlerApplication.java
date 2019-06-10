package com.bosch.pai.retail.filehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class FileHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileHandlerApplication.class, args);
    }

}