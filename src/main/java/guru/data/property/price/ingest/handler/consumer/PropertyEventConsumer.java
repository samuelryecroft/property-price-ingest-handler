package guru.data.property.price.ingest.handler.consumer;


import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.serivce.PropertyAlignmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
@Slf4j
public class PropertyEventConsumer {

    private final PropertyAlignmentService propertyAlignmentService;

    @RabbitListener(queues = "property-price-paid")
    public void pricePaidImportHandler(PricePaidTransactionInput pricePaidTransaction) {

        propertyAlignmentService.alignPropertyRecord(pricePaidTransaction);

    }
}
