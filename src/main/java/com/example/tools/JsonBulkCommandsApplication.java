package com.example.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class JsonBulkCommandsApplication {

  public static void main(String[] args) {
    SpringApplication.run(JsonBulkCommandsApplication.class, args);
  }

}
