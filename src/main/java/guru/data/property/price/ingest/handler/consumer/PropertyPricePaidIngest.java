package guru.data.property.price.ingest.handler.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.serivce.PropertyAlignmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PropertyPricePaidIngest {

  private final PropertyAlignmentService propertyAlignmentService;

  @KafkaListener(id = "property-data-ingest", topics = "property-transactions", containerFactory = "kafkaListenerContainerFactory", concurrency = "24")
  public void alignPropertyData(String pricePaidTransactionInput) {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    PricePaidTransactionInput input = null;
    try {
      input = objectMapper.readValue(pricePaidTransactionInput, PricePaidTransactionInput.class);
    } catch (JsonProcessingException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    }

    propertyAlignmentService.alignPropertyRecord(input);
  }

}
