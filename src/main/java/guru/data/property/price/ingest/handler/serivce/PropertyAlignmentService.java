package guru.data.property.price.ingest.handler.serivce;


import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
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
        .ifPresentOrElse(property::setLocation,
            () -> log.warn("Unable to find postcode lookup for property: {}", property.getId()));

    propertyRepository.save(property);
  }

  private void updatePropertyRecord(Property savedRecord,
      PricePaidTransactionInput pricePaidTransactionInput) {

    final InputAction inputAction = pricePaidTransactionInput.getInputAction();
    final Set<SaleTransaction> saleTransactions = pricePaidTransactionInput.getProperty()
        .getTransactions();

    final LocalDate latestTransactionDate = getLatestDataDateFromTransactions(savedRecord,
        pricePaidTransactionInput.getProperty());

    switch (inputAction) {
      case ADDITION ->
          propertyRepository.addTransactionsForProperty(savedRecord.getId(), saleTransactions, latestTransactionDate);
      case CHANGE ->
          propertyRepository.updateTransactionForProperty(savedRecord.getId(), saleTransactions, latestTransactionDate);
      case DELETE ->
          propertyRepository.removeTransactionsForProperty(savedRecord.getId(), saleTransactions, latestTransactionDate);
    }

    if (Objects.isNull(savedRecord.getLocation())) {
      postCodeGeoCodingService.getGeoLocationFromPostCode(savedRecord.getAddress().getPostCode())
          .ifPresentOrElse(
              location -> propertyRepository.updatePropertyLocation(savedRecord, location),
              () -> log.warn("Unable to find postcode lookup for property: {}", savedRecord.getId()));
    }

    if (savedRecord.getPropertyType() != pricePaidTransactionInput.getProperty().getPropertyType()) {
      propertyRepository.updatePropertyDetails(pricePaidTransactionInput.getProperty());
    }
  }

  private LocalDate getLatestDataDateFromTransactions (Property savedProperty, Property transactionProperty) {
    return savedProperty.getLatestDataDate().isAfter(transactionProperty.getLatestDataDate())
        ? savedProperty.getLatestTransactionDate()
        : transactionProperty.getLatestTransactionDate();
  }
}
