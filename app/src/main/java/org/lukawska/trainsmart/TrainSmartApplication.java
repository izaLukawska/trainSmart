package org.lukawska.trainsmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableRetry
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class TrainSmartApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainSmartApplication.class, args);
    }
}
