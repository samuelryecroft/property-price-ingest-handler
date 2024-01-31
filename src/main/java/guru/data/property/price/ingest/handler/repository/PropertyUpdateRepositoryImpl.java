package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@AllArgsConstructor
@Slf4j
@Component
public class PropertyUpdateRepositoryImpl implements PropertyUpdateRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void removeTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions) {

      transactions.forEach(transaction -> {
          mongoTemplate.updateFirst(
                  query(where("_id").is(propertyId)),
                  new Update()
                          .pull("transactions", new Query(Criteria.where("_id").is(transaction.getId())))
                          .set("lastUpdated", LocalDate.now()),
                  Property.class);

      });
    }

    @Override
    public void updateTransactionForProperty(String propertyId, Set<SaleTransaction> transactions) {
        removeTransactionsForProperty(propertyId, transactions);

        mongoTemplate.updateFirst(
                query(where("_id").is(propertyId)),
                new Update()
                        .push("transactions").each(transactions)
                        .set("lastUpdated", LocalDate.now()),
                Property.class);
    }

    @Override
    public void addTransactionsForProperty(String propertyId, Set<SaleTransaction> transactions) {

        transactions.forEach(transaction -> {
            mongoTemplate.updateFirst(
                    query(where("_id").is(propertyId)
                            .and("transactions").not().elemMatch(Criteria.where("_id").is(transaction.getId()))),
                    new Update()
                            .push("transactions", transaction)
                            .set("lastUpdated", LocalDate.now()),
                    Property.class);
        });

    }

    @Override
    public void updatePropertyDetails(Property property) {

        mongoTemplate.updateFirst(
                query(where("_id").is(property).and("latestDataDate").lte(property.getLatestTransactionDate())),
                new Update()
                        .set("propertyType", property.getPropertyType())
                        .set("latestDataDate", property.getLatestTransactionDate())
                        .set("lastUpdated", LocalDate.now()),
                Property.class
        );
    }


}
