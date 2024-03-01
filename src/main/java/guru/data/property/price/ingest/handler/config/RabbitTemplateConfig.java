package guru.data.property.price.ingest.handler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit-mq-consumer")
@Getter
@Setter
public class RabbitTemplateConfig {

  private int prefetchCount;

  private int concurrentConsumers;

  @Bean
  public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter rabbitMessageConverter) {
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
    simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
    simpleRabbitListenerContainerFactory.setPrefetchCount(prefetchCount);
    simpleRabbitListenerContainerFactory.setConcurrentConsumers(concurrentConsumers);
    simpleRabbitListenerContainerFactory.setMessageConverter(rabbitMessageConverter);

    System.out.println("Factory built");
    return simpleRabbitListenerContainerFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitMqConnectionFactory,
      Jackson2JsonMessageConverter rabbitMessageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitMqConnectionFactory);
    rabbitTemplate.setMessageConverter(rabbitMessageConverter);

    System.out.println("Template built");
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
    System.out.println("Converter built");
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @Bean
  public ObjectMapper objectMapper() {
    System.out.println("Mapper built");
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }

}