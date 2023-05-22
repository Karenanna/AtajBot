package pro.sky.atajbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtajBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtajBotApplication.class, args);
    }

}
