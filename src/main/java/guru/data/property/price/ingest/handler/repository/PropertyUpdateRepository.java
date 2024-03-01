package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.cglib.core.Local;

public interface PropertyUpdateRepository {

  void removeTransactionsForProperty(String propertyId, Set<SaleTransaction> transactionsId, LocalDate latestDataDate);

  void updateTransactionForProperty(String propertyId, Set<SaleTransaction> saleTransactions, LocalDate latestDataDate);

  void addTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate);

  void updatePropertyDetails(Property property);

  void updatePropertyLocation(Property property, GeoLocation geoLocation);
}
