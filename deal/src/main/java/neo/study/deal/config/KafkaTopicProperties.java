package neo.study.deal.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "spring.kafka")
@AllArgsConstructor
@Getter
public class KafkaTopicProperties {

    private Map<String, TopicConfig> topics;

    public static record TopicConfig(String name, Integer partitions, Integer replicas) {
    }
}
