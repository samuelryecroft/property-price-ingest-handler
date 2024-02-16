package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;

import java.util.List;
import java.util.Set;

public interface PropertyUpdateRepository {

    void removeTransactionsForProperty(String propertyId, Set<SaleTransaction> transactionsId);
    void updateTransactionForProperty(String propertyId, Set<SaleTransaction> saleTransactions);
    void addTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions);
    void updatePropertyDetails(Property property);
    void updatePropertyLocation(Property property, GeoLocation geoLocation);
}
