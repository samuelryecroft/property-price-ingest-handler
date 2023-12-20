package guru.data.property.price.ingest.handler.serivce;

import guru.data.property.price.ingest.handler.model.input.InputAction;
import guru.data.property.price.ingest.handler.model.input.PricePaidTransactionInput;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.repository.PropertyRepository;
import java.util.Optional;
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

  @InjectMocks
  PropertyAlignmentService propertyAlignmentService;


  @Test
  void alignPropertyRecordOnNewPropertyForAddition() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyUpdate.addNewTransactions(any())).thenReturn(false);
    when(propertyUpdate.mergePropertyInformation(propertyUpdate)).thenReturn(false);

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(propertyUpdate);

    verify(propertyRepository).save(propertyUpdate);
    verify(propertyUpdate).addNewTransactions(any());
    verify(propertyUpdate).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository).save(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnNewPropertyForUpdate() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.CHANGE);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyUpdate.updateTransactions(any())).thenReturn(false);
    when(propertyUpdate.mergePropertyInformation(propertyUpdate)).thenReturn(false);

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(propertyUpdate);

    verify(propertyRepository).save(propertyUpdate);
    verify(propertyUpdate).updateTransactions(any());
    verify(propertyUpdate).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository).save(propertyUpdate);
  }


  @Test
  void alignPropertyRecordOnNewPropertyForDeletion() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.DELETE);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.empty());

    when(propertyUpdate.deleteTransactions(any())).thenReturn(false);
    when(propertyUpdate.mergePropertyInformation(propertyUpdate)).thenReturn(false);

    when(propertyRepository.save(propertyUpdate)).thenReturn(propertyUpdate);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(propertyUpdate);


    verify(propertyUpdate).deleteTransactions(any());
    verify(propertyUpdate).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository).save(propertyUpdate);
  }

  @Test
  void alignPropertyRecordOnExistingPropertyWhereTransactionUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));

    when(existingProperty.addNewTransactions(any())).thenReturn(true);
    when(existingProperty.mergePropertyInformation(propertyUpdate)).thenReturn(false);

    when(propertyRepository.save(existingProperty)).thenReturn(existingProperty);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(existingProperty);

    verify(existingProperty).addNewTransactions(any());
    verify(existingProperty).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository).save(existingProperty);

  }

  @Test
  void alignPropertyRecordOnExistingPropertyWherePropertyUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));

    when(existingProperty.addNewTransactions(any())).thenReturn(false);
    when(existingProperty.mergePropertyInformation(propertyUpdate)).thenReturn(true);

    when(propertyRepository.save(existingProperty)).thenReturn(existingProperty);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(existingProperty);

    verify(existingProperty).addNewTransactions(any());
    verify(existingProperty).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository).save(existingProperty);

  }

  @Test
  void alignPropertyRecordOnExistingPropertyWhereNoUpdateRequired() {
    final PricePaidTransactionInput pricePaidTransactionInput = getPricePaidTransactionInput(InputAction.ADDITION);

    when(propertyUpdate.getId()).thenReturn(PROPERTY_ID);
    when(propertyRepository.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty));

    when(existingProperty.addNewTransactions(any())).thenReturn(false);
    when(existingProperty.mergePropertyInformation(propertyUpdate)).thenReturn(false);

    final Property result = propertyAlignmentService.alignPropertyRecord(pricePaidTransactionInput);

    assertThat(result).isEqualTo(existingProperty);

    verify(existingProperty).addNewTransactions(any());
    verify(existingProperty).mergePropertyInformation(propertyUpdate);
    verify(propertyRepository, never()).save(existingProperty);

  }

  private PricePaidTransactionInput getPricePaidTransactionInput(InputAction inputAction) {
    return PricePaidTransactionInput.builder().inputAction(inputAction).property(propertyUpdate).build();
  }

}