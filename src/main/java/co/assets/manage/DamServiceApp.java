package co.assets.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {
        "co.assets.manage.domain",
})
@EnableJpaRepositories(basePackages = "co.assets.manage.infrastructure.repository")
@SpringBootApplication
public class DamServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(DamServiceApp.class, args);
    }

}
