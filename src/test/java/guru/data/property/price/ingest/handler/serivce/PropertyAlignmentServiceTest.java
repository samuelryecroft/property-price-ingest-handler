package guru.data.property.price.ingest.handler.serivce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Address;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.PropertyType;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyAlignmentServiceTest {

  private static final String PROPERTY_ID = "property-id-value";

  private static final String POSTCODE = "BS32 0AL";

  private static final LocalDate DATEUPDATED = LocalDate.now();

  private static final Set<SaleTransaction> transactions = Set.of(
      SaleTransaction.builder().id("sale-transaction").dateOfTransfer(DATEUPDATED).build());

  private static final GeoLocation location = GeoLocation.builder()
      .type("Point")
      .coordinates(new double[]{51.545754d, -2.559503d})
      .build();

  private final Property existingProperty = Property.builder()
      .id(PROPERTY_ID)
      .address(Address.builder().postCode(POSTCODE).build())
      .transactions(transactions)
      .propertyType(PropertyType.DETACHED)
      .build();

  private final Property propertyUpdate = Property.builder()
      .id(PROPERTY_ID)
      .address(Address.builder().postCode(POSTCODE).build())
      .transactions(transactions)
      .propertyType(PropertyType.DETACHED)
      .build();
  @InjectMocks
  PropertyAlignmentService propertyAlignmentService;
  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private PostCodeGeoCodingService postCodeGeoCodingService;
  @Captor
  private ArgumentCaptor<Property> propertyArgumentCaptor;

  @Test
  void alignPropertyRecordOnNewPropertyForAddition() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.ADDITION);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).save(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnNewPropertyForAdditionWhenPostcodeLocationFound() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.ADDITION);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(postCodeGeoCodingService.getGeoLocationFromPostCode(POSTCODE)).thenReturn(
        Optional.of(location));

    when(propertyRepository.save(propertyArgumentCaptor.capture())).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).save(propertyUpdate);

    Property savedProperty = propertyArgumentCaptor.getValue();

    assertThat(savedProperty).usingRecursiveAssertion()
        .isEqualTo(propertyUpdate.withLocation(location));

  }

  @Test
  void alignPropertyRecordOnNewPropertyForUpdate() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.CHANGE);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).save(propertyUpdate);
  }


  @Test
  void alignPropertyRecordOnNewPropertyForDeletion() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.DELETE);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyRepository.save(propertyArgumentCaptor.capture())).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    Property savedProperty = propertyArgumentCaptor.getValue();

    assertThat(savedProperty).usingRecursiveAssertion()
        .isEqualTo(propertyUpdate.withTransactions(Set.of()));
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWhereTransactionUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.ADDITION);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).addTransactionsForProperty(PROPERTY_ID, transactions, DATEUPDATED);
    verify(propertyRepository, never()).updatePropertyDetails(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWhereTransactionUpdateRequiredAndLocationFound() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.ADDITION);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));

    when(postCodeGeoCodingService.getGeoLocationFromPostCode(POSTCODE)).thenReturn(Optional.of(location));
    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(postCodeGeoCodingService).getGeoLocationFromPostCode(POSTCODE);

    verify(propertyRepository).addTransactionsForProperty(PROPERTY_ID, transactions, DATEUPDATED);
    verify(propertyRepository, never()).updatePropertyDetails(propertyUpdate);
    verify(propertyRepository).updatePropertyLocation(propertyUpdate, location);
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWherePropertyUpdateRequiredForAdditionalTransaction() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.ADDITION);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(
        Optional.of(existingProperty.withPropertyType(PropertyType.TERRACED)));

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).addTransactionsForProperty(PROPERTY_ID, transactions, DATEUPDATED);
    verify(propertyRepository).updatePropertyDetails(propertyUpdate);

  }

  @Test
  void alignPropertyRecordOnExistingPropertyWherePropertyUpdateRequiredForChangeTransaction() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.CHANGE);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(
        Optional.of(existingProperty.withPropertyType(PropertyType.TERRACED)));

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).updateTransactionForProperty(PROPERTY_ID, transactions, DATEUPDATED);
    verify(propertyRepository).updatePropertyDetails(propertyUpdate);

  }

  @Test
  void alignPropertyRecordOnExistingPropertyWherePropertyUpdateRequiredForDeleteTransaction() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(
        InputAction.DELETE);

    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(
        Optional.of(existingProperty.withPropertyType(PropertyType.TERRACED)));

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).removeTransactionsForProperty(PROPERTY_ID, transactions, DATEUPDATED);
    verify(propertyRepository).updatePropertyDetails(propertyUpdate);

  }

  private PricePaidTransactionInput getPricePaidTransactionInput(InputAction inputAction) {
    return PricePaidTransactionInput.builder().inputAction(inputAction).property(propertyUpdate)
        .build();
  }

}