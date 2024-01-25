package guru.data.property.price.ingest.handler.serivce;

import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.PropertyType;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyAlignmentServiceTest {

  private static final String PROPERTY_ID = "property-id-value";

  @Mock
  private PropertyRepository propertyRepository;

  @Mock
  private Property existingProperty;

  @Mock
  private Property propertyUpdate;

  private final Set<SaleTransaction> transactions = Set.of(SaleTransaction.builder().id("sale-transaction").build());

  @InjectMocks
  PropertyAlignmentService propertyAlignmentService;


  @Test
  void alignPropertyRecordOnNewPropertyForAddition() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());


    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);


    verify(propertyRepository).save(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnNewPropertyForUpdate() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.CHANGE);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).save(propertyUpdate);
  }


  @Test
  void alignPropertyRecordOnNewPropertyForDeletion() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.DELETE);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());


    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

   propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);


    verify(propertyRepository).save(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWhereTransactionUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(existingProperty.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));
    when(propertyUpdate.getTransactions()).thenReturn(transactions);

    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);


    verify(propertyRepository).addTransactionsForProperty(PROPERTY_ID, transactions);
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWherePropertyUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(existingProperty.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));
    when(propertyUpdate.getTransactions()).thenReturn(transactions);

    when(propertyUpdate.getPropertyType()).thenReturn(PropertyType.DETACHED);
    when(existingProperty.getPropertyType()).thenReturn(PropertyType.TERRACED);


    propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    verify(propertyRepository).addTransactionsForProperty(PROPERTY_ID, transactions);
    verify(propertyRepository).updatePropertyDetails(propertyUpdate);

  }

  private PricePaidTransactionInput getPricePaidTransactionInput(InputAction inputAction) {
    return PricePaidTransactionInput.builder().inputAction(inputAction).property(propertyUpdate).build();
  }

}