package com.apollo.course.kafka;

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

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Value("${course.kafka.server}")
    private String bootstrapServer;
    @Value("${course.kafka.topic}")
    private String topicName;
    @Value("${course.kafka.partitions}")
    private Integer numberOfPartitions;
    @Value("${course.kafka.replicas}")
    private Short numberOfReplicas;
    @Value("${course.kafka.retention}")
    private String retentionPeriod;
    @Value("${course.kafka.acks}")
    private String acks;
    @Value("${course.kafka.retries}")
    private Integer numberOfRetries;
    @Value("${course.kafka.requestimeout}")
    private String requestTimeout;
    @Value("${course.kafka.batch}")
    private String batchSize;
    @Value("${course.kafka.linger}")
    private String linger;
    @Value("${course.kafka.max-in-flight}")
    private String maxInFlight;
    @Value("${course.kafka.client-id}")
    private String clientId;
    @Value("${course.kafka.group-id}")
    private String groupId;
    @Value("${course.kafka.offset}")
    private String offsetConfig;

    @Bean
    NewTopic createCourseTopic() {
        return TopicBuilder
                .name(this.topicName)
                .partitions(this.numberOfPartitions)
                .replicas(this.numberOfReplicas)
                .config(TopicConfig.RETENTION_MS_CONFIG , this.retentionPeriod)
                .build();
    }

    @Bean
    KafkaSender courseKafkaSender() {
        final Properties senderProperties = new Properties();
        senderProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        senderProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , StringSerializer.class);
        senderProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , JsonSerializer.class);
        senderProperties.put(ProducerConfig.ACKS_CONFIG , this.acks);
        senderProperties.put(ProducerConfig.RETRIES_CONFIG , this.numberOfRetries);
        senderProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG , this.requestTimeout);
        senderProperties.put(ProducerConfig.BATCH_SIZE_CONFIG , this.batchSize);
        senderProperties.put(ProducerConfig.LINGER_MS_CONFIG , this.linger);
        senderProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION , this.maxInFlight);

        return new DefaultKafkaSender<>(ProducerFactory.INSTANCE , SenderOptions.create(senderProperties));
    }

    @Bean
    KafkaReceiver courseKafkaReceiver() {
        final Properties receiverProperties = new Properties();
        receiverProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        receiverProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        receiverProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , JsonDeserializer.class);
        receiverProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , true);
        receiverProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , this.offsetConfig);

        return new DefaultKafkaReceiver<>(ConsumerFactory.INSTANCE , ReceiverOptions.create(receiverProperties));
    }
}
