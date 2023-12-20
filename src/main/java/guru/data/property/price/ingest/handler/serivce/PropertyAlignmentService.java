package guru.data.property.price.ingest.handler.serivce;

import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PropertyAlignmentService {

  private final PropertyRepository propertyRepository;

  public Property alignPropertyRecord(PricePaidTransactionInput pricePaidTransactionInput) {
    final String propertyId = pricePaidTransactionInput.getProperty().getId();

    final Optional<Property> propertyOption = propertyRepository.findById(propertyId);

    final Property propertyRecord = propertyOption.orElse(pricePaidTransactionInput.getProperty());

    final boolean propertyHasUpdates = updateProperty(propertyRecord, pricePaidTransactionInput);

    if (propertyOption.isEmpty() || propertyHasUpdates) {
      return propertyRepository.save(propertyRecord);
    }

    return propertyRecord;
  }

  private boolean alignPropertyTransactions(Property property, PricePaidTransactionInput pricePaidTransactionInput) {
    final InputAction inputAction = pricePaidTransactionInput.getInputAction();

    return switch (inputAction){
      case ADDITION -> property.addNewTransactions(pricePaidTransactionInput.getProperty().getTransactions());
      case CHANGE -> property.updateTransactions(pricePaidTransactionInput.getProperty().getTransactions());
      case DELETE -> property.deleteTransactions(property.getTransactions());
    };
  }

  private boolean updateProperty(Property property, PricePaidTransactionInput pricePaidTransactionInput) {

    final boolean propertyDetailsChange = property.mergePropertyInformation(pricePaidTransactionInput.getProperty());
    final boolean transactionChange = alignPropertyTransactions(property, pricePaidTransactionInput);

    return propertyDetailsChange || transactionChange;

  }

}
