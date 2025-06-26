package neo.study.deal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${services.calculator.base-url}")
    private String baseUrl;

    @Bean
    RestClient calculatorRestClient() {
        return RestClient.builder().baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json").build();
    }

}
