package com.apollo.course.kafka;

import com.apollo.course.model.Course;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.Collections;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Value("${course.kafka.server}")
    String bootstrapServer;
    @Value("${course.kafka.topic}")
    String topicName;
    @Value("${course.kafka.partitions}")
    Integer numberOfPartitions;
    @Value("${course.kafka.replicas}")
    Short numberOfReplicas;

    @Bean
    NewTopic createCourseTopic() {
        return TopicBuilder
                .name(this.topicName)
                .partitions(this.numberOfPartitions)
                .replicas(this.numberOfReplicas)
                .config(TopicConfig.RETENTION_MS_CONFIG , "-1")
                .build();
    }

    @Bean
    KafkaSender<String , Course> courseKafkaSender() {
        final Properties courseSenderProperties = new Properties();
        courseSenderProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        courseSenderProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , StringSerializer.class);
        courseSenderProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , JsonSerializer.class);
        courseSenderProperties.put(ProducerConfig.ACKS_CONFIG , "all");
        courseSenderProperties.put(ProducerConfig.RETRIES_CONFIG , 10);
        courseSenderProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG , "5000");
        courseSenderProperties.put(ProducerConfig.BATCH_SIZE_CONFIG , "163850");
        courseSenderProperties.put(ProducerConfig.LINGER_MS_CONFIG , "100");
        courseSenderProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION , "1");

        return new DefaultKafkaSender<String , Course>(ProducerFactory.INSTANCE , SenderOptions.create(courseSenderProperties));
    }

    @Bean
    KafkaReceiver<String , Course> courseKafkaReceiver() {
        final Properties courseReceiverProperties = new Properties();
        courseReceiverProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        courseReceiverProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        courseReceiverProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , JsonDeserializer.class);
        courseReceiverProperties.put(ConsumerConfig.CLIENT_ID_CONFIG , "course-client");
        courseReceiverProperties.put(ConsumerConfig.GROUP_ID_CONFIG , "course-group");
        courseReceiverProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , true);
        courseReceiverProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , "latest");

        ReceiverOptions<String  , Course> courseReceiverOptions = ReceiverOptions.create(courseReceiverProperties);
        return new DefaultKafkaReceiver<String , Course>(ConsumerFactory.INSTANCE , courseReceiverOptions.subscription(Collections.singleton(this.topicName)));
    }
}
