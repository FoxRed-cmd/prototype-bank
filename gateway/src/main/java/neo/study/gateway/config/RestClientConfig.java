package neo.study.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.deal.base-url}")
    private String dealBaseUrl;

    @Value("${services.statement.base-url}")
    private String statementBaseUrl;

    @Bean
    RestClient restClientToDeal() {
        return RestClient.builder()
                .baseUrl(dealBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json").build();
    }

    @Bean
    RestClient restClientToStatement() {
        return RestClient.builder()
                .baseUrl(statementBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json").build();
    }
}
