package guru.data.property.price.ingest.handler.repository;

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
          query(where("_id").is(propertyId)),
          new Update()
              .pull("transactions", new Query(Criteria.where("_id").is(transaction.getId())))
              .set("lastUpdated", LocalDate.now())
              .set("latestDataDate", latestDataDate),
          Property.class)
    );
  }

  @Override
  public void updateTransactionForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate) {
    removeTransactionsForProperty(propertyId, transactions, latestDataDate);

    mongoTemplate.updateFirst(
        query(where("_id").is(propertyId)),
        new Update()
            .push("transactions").each(transactions)
            .set("lastUpdated", LocalDate.now())
            .set("latestDataDate", latestDataDate),
        Property.class);
  }

  @Override
  public void addTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions, LocalDate latestDataDate) {

    transactions.forEach(transaction ->
      mongoTemplate.updateFirst(
          query(where("_id").is(propertyId)
              .and("transactions").not().elemMatch(Criteria.where("_id").is(transaction.getId()))),
          new Update()
              .push("transactions", transaction)
              .set("lastUpdated", LocalDate.now())
              .set("latestDataDate", latestDataDate),
          Property.class)
    );

  }

  @Override
  public void updatePropertyDetails(Property property) {

    mongoTemplate.updateFirst(
        query(where("_id").is(property.getId()).and("latestDataDate")
            .lte(property.getLatestTransactionDate())),
        new Update()
            .set("propertyType", property.getPropertyType())
            .set("latestDataDate", property.getLatestTransactionDate()),
        Property.class
    );
  }

  @Override
  public void updatePropertyLocation(Property property, GeoLocation geoLocation) {
    mongoTemplate.updateFirst(
        query(where("_id").is(property.getId()).and("latestDataDate")
            .lte(property.getLatestTransactionDate())),
        new Update()
            .set("location", geoLocation),
        Property.class
    );
  }
}
