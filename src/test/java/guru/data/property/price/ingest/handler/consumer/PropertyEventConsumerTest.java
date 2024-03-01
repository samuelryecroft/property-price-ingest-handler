package guru.data.property.price.ingest.handler.consumer;

import static org.mockito.Mockito.verify;

import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.serivce.PropertyAlignmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyEventConsumerTest {

  @Mock
  private PropertyAlignmentService propertyAlignmentService;

  @InjectMocks
  private PropertyEventConsumer propertyEventConsumer;

  @Mock
  private PricePaidTransactionInput pricePaidTransactionInput;


   @Test
   void consumerInvokesExpectedMethods() {

   propertyEventConsumer.pricePaidImportHandler(pricePaidTransactionInput);

   verify(propertyAlignmentService).alignPropertyRecord(pricePaidTransactionInput);
   }

}