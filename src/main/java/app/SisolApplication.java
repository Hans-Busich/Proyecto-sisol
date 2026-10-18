package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "app", "controller", "service", "dao"
})
public class SisolApplication {
    public static void main(String[] args) {
        SpringApplication.run(SisolApplication.class, args);
    }
}