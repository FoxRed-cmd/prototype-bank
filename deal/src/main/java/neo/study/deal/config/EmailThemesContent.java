package neo.study.deal.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "spring.email")
@AllArgsConstructor
@Getter
public class EmailThemesContent {
    private Map<String, String> themes;

}
