package neo.study.deal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${allowed-services.statement}")
    private String allowedStatement;

    @Value("${allowed-services.gateway}")
    private String allowedGateway;

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedStatement, allowedGateway)
                        .allowedMethods("GET", "PUT", "POST")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
