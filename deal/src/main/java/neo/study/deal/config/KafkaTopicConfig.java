package neo.study.deal.config;

import java.util.List;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final KafkaTopicProperties topicProperties;

    @Bean
    List<NewTopic> kafkaTopics() {
        return topicProperties.getTopics()
                .values()
                .stream()
                .map(t -> TopicBuilder
                        .name(t.name())
                        .partitions(t.partitions())
                        .replicas(t.replicas())
                        .build())
                .toList();
    }
}
