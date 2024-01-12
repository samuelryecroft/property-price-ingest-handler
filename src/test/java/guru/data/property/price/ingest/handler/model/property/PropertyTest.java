package guru.data.property.price.ingest.handler.model.property;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PropertyTest {

  @Test
  void addTransactionsToPropertyWithNoExistingTransactions() {
    final Property property = Property.builder().build();

    final Set<SaleTransaction> newTransactions = Set.of(
        SaleTransaction.builder().id("transaction-id-1").build()
    );

    final boolean result = property.addNewTransactions(newTransactions);

    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).containsAll(newTransactions);
  }

  @Test
  void addTransactionsToPropertyWithExistingTransactions() {
    final Set<SaleTransaction> existingTransactions = new HashSet<>(Collections.singletonList(
        SaleTransaction.builder().id("transaction-id-1").build()
    ));


    final Property property = Property.builder().transactions(existingTransactions).build();

    final Set<SaleTransaction> newTransactions = Set.of(
        SaleTransaction.builder().id("transaction-id-2").build()
    );

    final boolean result = property.addNewTransactions(newTransactions);

    final Set<SaleTransaction> expectedTransactionSet = Stream.concat(
            existingTransactions.stream(), newTransactions.stream())
        .collect(Collectors.toSet());

    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).containsAll(expectedTransactionSet);
  }

  @Test
  void addTransactionsToPropertyWithDuplicateTransaction() {
    final Set<SaleTransaction> existingTransactions = new HashSet<>(Collections.singletonList(
        SaleTransaction.builder().id("transaction-id-1").build()
    ));

    final Property property = Property.builder().transactions(existingTransactions).build();

    final Set<SaleTransaction> newTransactions = Set.of(
        SaleTransaction.builder().id("transaction-id-1").build()
    );

    final boolean result =property.addNewTransactions(newTransactions);

    assertThat(result).isFalse();
    Assertions.assertThat(property.getTransactions()).containsAll(existingTransactions);
  }

  @Test
  void deleteTransactionsWithNoPropertyTransactions() {
    final Property property = Property.builder().build();

    final Set<SaleTransaction> transactionsToDelete = Set.of(
        SaleTransaction.builder().id("transaction-id-1").build()
    );

    final boolean result = property.deleteTransactions(transactionsToDelete);

    assertThat(result).isFalse();
    Assertions.assertThat(property.getTransactions()).isEmpty();

  }

  @Test
  void deleteTransactionsWhenTransactionExists() {
    final Set<SaleTransaction> existingTransactions = new HashSet<>(Collections.singletonList(
        SaleTransaction.builder().id("transaction-id-1").build()
    ));

    final Property property = Property.builder().transactions(existingTransactions).build();

    final Set<SaleTransaction> transactionsToDelete = Set.of(
        SaleTransaction.builder().id("transaction-id-1").build()
    );

    final boolean result = property.deleteTransactions(transactionsToDelete);

    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).isEmpty();
  }

  @Test
  void updateTransactionWhenNoTransactionExists() {
    final Property property = Property.builder().build();

    final Set<SaleTransaction> transactionsToUpdate = Set.of(
        SaleTransaction.builder().id("transaction-id-1").build()
    );

    final boolean result = property.updateTransactions(transactionsToUpdate);

    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).containsAll(transactionsToUpdate);
  }

  @Test
  void updateTransactionWhenUpdatedTransactionDoesNotExists() {
    final Set<SaleTransaction> existingTransactions = new HashSet<>(Collections.singletonList(
        SaleTransaction.builder().id("transaction-id-1").build()
    ));

    final Property property = Property.builder().transactions(existingTransactions).build();

    final Set<SaleTransaction> transactionsToUpdate = Set.of(
        SaleTransaction.builder().id("transaction-id-2").build()
    );

    final boolean result = property.updateTransactions(transactionsToUpdate);

    final Set<SaleTransaction> expectedTransactionSet = Stream.concat(
            existingTransactions.stream(), transactionsToUpdate.stream())
        .collect(Collectors.toSet());


    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).containsAll(expectedTransactionSet);
  }

  @Test
  void updateTransactionWhenUpdatedTransactionsExist() {
    final Set<SaleTransaction> existingTransactions = new HashSet<>(Collections.singletonList(
        SaleTransaction.builder().id("transaction-id-1").value(1000).build()
    ));

    final Property property = Property.builder().transactions(existingTransactions).build();

    final Set<SaleTransaction> transactionsToUpdate = Set.of(
        SaleTransaction.builder().id("transaction-id-1").value(2000).build()
    );

    final boolean result = property.updateTransactions(transactionsToUpdate);

    assertThat(result).isTrue();
    Assertions.assertThat(property.getTransactions()).hasSize(1);
    assertThat(property.getTransactions().stream().findFirst().get().getValue()).isEqualTo(2000);
  }

  @Test
  void mergePropertyInformationWithNoDatesAcceptsUpdates() {
    final Property existingPropertyData = Property.builder().propertyType(PropertyType.DETACHED).build();

    final Property mergingPropertyData = Property.builder().propertyType(PropertyType.OTHER).build();

    final boolean result = existingPropertyData.mergePropertyInformation(mergingPropertyData);

    assertThat(result).isTrue();
    assertThat(existingPropertyData.getPropertyType()).isEqualTo(PropertyType.OTHER);
    assertThat(existingPropertyData.getLastUpdated()).isEqualTo(LocalDate.now());
  }

  @Test
  void mergePropertyInformationWithOlderMergeDataRejectsChanges() {
    final Property existingPropertyData = Property.builder()
        .propertyType(PropertyType.DETACHED)
        .latestDataDate(LocalDate.now())
        .lastUpdated(LocalDate.now())
        .build();

    final Property mergingPropertyData = Property.builder()
            .propertyType(PropertyType.OTHER)
            .latestDataDate(LocalDate.EPOCH)
            .lastUpdated(LocalDate.EPOCH)
            .build();

    final boolean result = existingPropertyData.mergePropertyInformation(mergingPropertyData);

    assertThat(result).isFalse();
    assertThat(existingPropertyData.getPropertyType()).isEqualTo(PropertyType.DETACHED);
    assertThat(existingPropertyData.getLatestDataDate()).isEqualTo(LocalDate.now());
    assertThat(existingPropertyData.getLastUpdated()).isEqualTo(LocalDate.now());

  }

  @Test
  void mergePropertyInformationWithNewerMergeDataAcceptsChanges() {
    final Property existingPropertyData = Property.builder()
        .propertyType(PropertyType.DETACHED)
        .latestDataDate(LocalDate.EPOCH)
        .lastUpdated(LocalDate.EPOCH)
        .build();

    final Property mergingPropertyData = Property.builder()
        .propertyType(PropertyType.OTHER)
        .transactions(
            Set.of(
                SaleTransaction.builder().dateOfTransfer(LocalDate.now()).build()
            )
        )
        .latestDataDate(LocalDate.now())
        .build();

    final boolean result = existingPropertyData.mergePropertyInformation(mergingPropertyData);

    assertThat(result).isTrue();
    assertThat(existingPropertyData.getPropertyType()).isEqualTo(PropertyType.OTHER);
    assertThat(existingPropertyData.getLatestDataDate()).isEqualTo(LocalDate.now());
    assertThat(existingPropertyData.getLastUpdated()).isEqualTo(LocalDate.now());

  }

  @Test
  void mergePropertyInformationWithNoChanges() {
    final Property existingPropertyData = Property.builder()
        .propertyType(PropertyType.DETACHED)
        .latestDataDate(LocalDate.EPOCH)
        .lastUpdated(LocalDate.EPOCH)
        .build();

    final Property mergingPropertyData = Property.builder()
        .propertyType(PropertyType.DETACHED)
        .transactions(
            Set.of(
                SaleTransaction.builder().dateOfTransfer(LocalDate.now()).build()
            )
        )
        .build();

    final boolean result = existingPropertyData.mergePropertyInformation(mergingPropertyData);

    assertThat(result).isFalse();
    assertThat(existingPropertyData.getPropertyType()).isEqualTo(PropertyType.DETACHED);
    assertThat(existingPropertyData.getLatestDataDate()).isEqualTo(LocalDate.EPOCH);
    assertThat(existingPropertyData.getLastUpdated()).isEqualTo(LocalDate.EPOCH);

  }

  @Test
  void getLatestTransactionDateReturnsEpochValueWhenNoTransactionsPresent() {
    final Property property = Property.builder().build();
    final LocalDate result = property.getLatestTransactionDate();

    assertThat(result).isEqualTo(LocalDate.EPOCH);
  }

  @Test
  void getLatestTransactionDateReturnsTheLargestInstanceValueFromMultipleTransactions() {
    final Set<SaleTransaction> transactions = Set.of(
        SaleTransaction.builder().id("transaction-1").dateOfTransfer(LocalDate.MIN).build(),
        SaleTransaction.builder().id("transaction-2").dateOfTransfer(LocalDate.now()).build(),
        SaleTransaction.builder().id("transaction-3").dateOfTransfer(LocalDate.MAX).build()
    );

    final Property property = Property.builder().transactions(transactions).build();
    final LocalDate result = property.getLatestTransactionDate();

    assertThat(result).isEqualTo(LocalDate.MAX);
  }
}