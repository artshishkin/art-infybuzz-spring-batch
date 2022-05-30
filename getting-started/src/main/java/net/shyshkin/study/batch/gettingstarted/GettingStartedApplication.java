package net.shyshkin.study.batch.gettingstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class GettingStartedApplication {

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }

}
