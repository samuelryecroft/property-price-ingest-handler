package guru.data.property.price.ingest.handler.model.property;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

  private String id;

  private Address address;

  private PropertyType propertyType;

  @Builder.Default
  private Set<SaleTransaction> transactions = new HashSet<>();

  private Location location;

  @Builder.Default
  private LocalDate latestDataDate = LocalDate.EPOCH;

  @Builder.Default
  private LocalDate lastUpdated = LocalDate.now();

  /**
   * Aligns property details with a comparable property ensuring that attributes of a property remain up to date.
   * @param property The property details to merge into this property instance.
   */
  public boolean mergePropertyInformation (Property property) {

    if (propertyType.equals(property.getPropertyType())) {
      return false;
    }

    if (property.getLatestTransactionDate().isAfter(latestDataDate) || property.getLatestTransactionDate().isEqual(latestDataDate)) {
      this.propertyType = property.getPropertyType();
      this.latestDataDate = property.getLatestTransactionDate();
      this.lastUpdated = LocalDate.now();
      return true;
    }

    return false;
  }

  /**
   * Adds a set of transactions to the existing transactions of a property, duplicate transactions will be removed.
   * @param transactionsToAdd The transactions to add to a property.
   * @return returns if the update resulted in a change to the underlying set of transactions.
   */
  public boolean addNewTransactions(Set<SaleTransaction> transactionsToAdd) {
    final boolean setUpdated = transactions.addAll(transactionsToAdd);

    if (setUpdated) {
      this.lastUpdated = LocalDate.now();
    }

    return setUpdated;
    }

  /**
   * Updates or adds transactions based on an update set, where a property does not have a transaction to update this
   * method acts in the same way as the add functionality.
   * @param transactionsToUpdate The set of transactions to update or add.
   * @return returns if the update resulted in the change of the underlying set.
   */
  public boolean updateTransactions(Set<SaleTransaction> transactionsToUpdate) {
    deleteTransactions(transactionsToUpdate);
    return addNewTransactions(transactionsToUpdate);
  }

  /**
   * Removes transactions from the transactions set based on a set of transactions to remove.
   * @param transactionsToRemove The set of transactions to remove.
   * @return returns if the deletion resulted a change in the underlying sets of transactions.
   */
  public boolean deleteTransactions(Set<SaleTransaction> transactionsToRemove) {
    final boolean setUpdated = transactions.removeAll(transactionsToRemove);

    if (setUpdated) {
      this.lastUpdated = LocalDate.now();
    }

    return setUpdated;
  }

  public LocalDate getLatestTransactionDate () {
    return transactions.stream()
        .max(Comparator.comparing(SaleTransaction::getDateOfTransfer))
        .map(SaleTransaction::getDateOfTransfer).orElse(LocalDate.EPOCH);
  }


}
