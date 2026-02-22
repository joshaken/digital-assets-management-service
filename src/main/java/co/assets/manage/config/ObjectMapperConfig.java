package co.assets.manage.config;

import co.assets.manage.utils.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper create() {
        return ObjectMapperFactory.createObjectMapper();
    }
}
