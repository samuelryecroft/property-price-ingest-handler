package guru.data.property.price.ingest.handler.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  public static final String EXCHANGE = "price-paid-ingest-exchange";
  public static final String INGEST_QUEUE = "price-paid-ingest";
  public static final String INGEST_DLQ = "price-paid-ingest-dlq";
  public static final String INGEST_ROUTING_KEY = "price-paid.ingest";

  public static final String INGEST_DEAD_LETTER_ROUTING_KEY = INGEST_ROUTING_KEY + ".dlq";

  @Bean
  public DirectExchange ingestExchange() {
    return ExchangeBuilder
        .directExchange(EXCHANGE)
        .build();
  }

  @Bean
  public Queue ingestQueue() {
    return QueueBuilder
        .durable(INGEST_QUEUE)
        .deadLetterRoutingKey(INGEST_ROUTING_KEY + ".dlq")
        .deadLetterExchange(EXCHANGE)
        .build();
  }

  @Bean
  public Queue ingestDeadLetterQueue() {
    return QueueBuilder
        .durable(INGEST_DLQ)
        .build();
  }

  @Bean
  public Binding ingestQueueBinding() {
    return BindingBuilder.bind(ingestQueue())
        .to(ingestExchange())
        .with(INGEST_ROUTING_KEY);
  }


  @Bean
  public Binding ingestDeadLetterQueueBinding() {
    return BindingBuilder.bind(ingestDeadLetterQueue())
        .to(ingestExchange())
        .with(INGEST_DEAD_LETTER_ROUTING_KEY);
  }

}
