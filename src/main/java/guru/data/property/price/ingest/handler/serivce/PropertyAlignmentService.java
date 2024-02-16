package guru.data.property.price.ingest.handler.serivce;


import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PropertyAlignmentService {

  private final PropertyRepository propertyRepository;

  private final PostCodeGeoCodingService postCodeGeoCodingService;

  public void alignPropertyRecord(PricePaidTransactionInput pricePaidTransactionInput) {
    final String propertyId = pricePaidTransactionInput.getProperty().getId();

    final Optional<Property> propertyOption = propertyRepository.findById(propertyId);

    propertyOption.ifPresentOrElse(
        pr -> updatePropertyRecord(pr, pricePaidTransactionInput),
        () -> createNewPropertyRecord(pricePaidTransactionInput));

  }

  private void createNewPropertyRecord(PricePaidTransactionInput pricePaidTransactionInput) {

    final Property property = pricePaidTransactionInput.getInputAction() == InputAction.DELETE ?
        pricePaidTransactionInput.getProperty().withTransactions(Set.of()) :
        pricePaidTransactionInput.getProperty();

    postCodeGeoCodingService.getGeoLocationFromPostCode(property.getAddress().getPostCode())
        .ifPresent(property::setLocation);

    propertyRepository.save(property);
  }

  private void updatePropertyRecord(Property savedRecord,
      PricePaidTransactionInput pricePaidTransactionInput) {

    final InputAction inputAction = pricePaidTransactionInput.getInputAction();
    final Set<SaleTransaction> saleTransactions = pricePaidTransactionInput.getProperty()
        .getTransactions();

    switch (inputAction) {
      case ADDITION ->
          propertyRepository.addTransactionsForProperty(savedRecord.getId(), saleTransactions);
      case CHANGE ->
          propertyRepository.updateTransactionForProperty(savedRecord.getId(), saleTransactions);
      case DELETE ->
          propertyRepository.removeTransactionsForProperty(savedRecord.getId(), saleTransactions);
    }

    if (Objects.isNull(savedRecord.getLocation())) {
      postCodeGeoCodingService.getGeoLocationFromPostCode(savedRecord.getAddress().getPostCode())
          .ifPresent(location -> propertyRepository.updatePropertyLocation(savedRecord, location));
    }

    if (savedRecord.getPropertyType() != pricePaidTransactionInput.getProperty()
        .getPropertyType()) {
      propertyRepository.updatePropertyDetails(pricePaidTransactionInput.getProperty());
    }
  }


}
