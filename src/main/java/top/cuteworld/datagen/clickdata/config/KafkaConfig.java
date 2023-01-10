package top.cuteworld.datagen.clickdata.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import top.cuteworld.datagen.clickdata.model.UserActionEmitter;
import top.cuteworld.datagen.clickdata.model.UserBehaviorItem;

import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaConfig {

    private final String TOPIC_NAME = "flink-user-behavior-data";

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(TOPIC_NAME)
                .partitions(4)
                .replicas(1)
                .build();
    }


    @Bean
    @Primary
    public ObjectMapper objectMapper() {
//        JavaTimeModule module = new JavaTimeModule();
//        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public UserActionEmitter emitter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        UserActionEmitter userActionEmitter = new UserActionEmitter(new Function<UserBehaviorItem, Void>() {
            @Override
            public Void apply(UserBehaviorItem userBehaviorItem) {
                try {
                    String s = objectMapper.writeValueAsString(userBehaviorItem);
//                    log.info("S is {}", s);
                    kafkaTemplate.send(TOPIC_NAME, s);
                } catch (JsonProcessingException e) {
//                    log.info("Json error", e);
                }
                return null;
            }

        });
        userActionEmitter.batchEmit();
        return userActionEmitter;
    }

}
