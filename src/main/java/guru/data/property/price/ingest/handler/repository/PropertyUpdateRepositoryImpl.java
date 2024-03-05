package guru.data.property.price.ingest.handler.repository;

import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LAST_UPDATED_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LATEST_DATA_DATE_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LOCATION_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.PROPERTY_ID_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.PROPERTY_TYPE_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.TRANSACTION_FIELD;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class PropertyUpdateRepositoryImpl implements PropertyUpdateRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public void removeTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate) {

    transactions.forEach(transaction ->
      mongoTemplate.updateFirst(
          query(where(PROPERTY_ID_FIELD).is(propertyId)),
          new Update()
              .pull(TRANSACTION_FIELD, new Query(Criteria.where("_id").is(transaction.getId())))
              .set(LAST_UPDATED_FIELD, LocalDate.now())
              .set(LATEST_DATA_DATE_FIELD, latestDataDate),
          Property.class)
    );
  }

  @Override
  public void updateTransactionForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate) {
    removeTransactionsForProperty(propertyId, transactions, latestDataDate);

    mongoTemplate.updateFirst(
        query(where(PROPERTY_ID_FIELD).is(propertyId)),
        new Update()
            .push(TRANSACTION_FIELD).each(transactions)
            .set(LAST_UPDATED_FIELD, LocalDate.now())
            .set(LATEST_DATA_DATE_FIELD, latestDataDate),
        Property.class);
  }

  @Override
  public void addTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate) {

    transactions.forEach(transaction ->
      mongoTemplate.updateFirst(
          query(where(PROPERTY_ID_FIELD).is(propertyId)
              .and(TRANSACTION_FIELD).not().elemMatch(Criteria.where("_id").is(transaction.getId()))),
          new Update()
              .push(TRANSACTION_FIELD, transaction)
              .set(LAST_UPDATED_FIELD, LocalDate.now())
              .set(LATEST_DATA_DATE_FIELD, latestDataDate),
          Property.class)
    );

  }

  @Override
  public void updatePropertyDetails(Property property) {

    mongoTemplate.updateFirst(
        query(where(PROPERTY_ID_FIELD).is(property.getId()).and(LATEST_DATA_DATE_FIELD)
            .lte(property.getLatestTransactionDate())),
        new Update()
            .set(PROPERTY_TYPE_FIELD, property.getPropertyType())
            .set(LATEST_DATA_DATE_FIELD, property.getLatestTransactionDate()),
        Property.class
    );
  }

  @Override
  public void updatePropertyLocation(Property property, GeoLocation geoLocation) {
    mongoTemplate.updateFirst(
        query(where(PROPERTY_ID_FIELD).is(property.getId()).and(LATEST_DATA_DATE_FIELD)
            .lte(property.getLatestTransactionDate())),
        new Update()
            .set(LOCATION_FIELD, geoLocation),
        Property.class
    );
  }
}
