package guru.data.property.price.ingest.handler.consumer;


import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.serivce.PropertyAlignmentService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PropertyEventConsumer {

    private final PropertyAlignmentService propertyAlignmentService;

    @RabbitListener(queues = "property-price-paid")
    public void pricePaidImportHandler(PricePaidTransactionInput pricePaidTransaction) {

        propertyAlignmentService.alignPropertyRecord(pricePaidTransaction);

    }
}
