package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ExtendWith(MockitoExtension.class)
class PropertyUpdateRepositoryImplTest {

    private final String PROPERTY_ID = "PROPERTY_ID";
    private final String TRANSACTION_ID = "TRANSACTION_ID";

    private final SaleTransaction saleTransaction = SaleTransaction.builder().id("TRANSACTION_ID").build();
    private final Set<SaleTransaction> saleTransactions = Set.of(
            saleTransaction
    );

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PropertyUpdateRepositoryImpl propertyUpdateRepository;


    @Test
    void removeTransactionsUsesExpectedMatchingCriteria() {

        propertyUpdateRepository.removeTransactionsForProperty(PROPERTY_ID, saleTransactions);

        verify(mongoTemplate).updateFirst(
                query(where("_id").is(PROPERTY_ID)),
                new Update()
                        .pull("transactions", query(Criteria.where("_id").is(TRANSACTION_ID)))
                        .set("lastUpdated", LocalDate.now()),
                Property.class
        );
    }

    @Test
     void updateTransactionsUsesExpectedMatchingCriteria() {

        propertyUpdateRepository.updateTransactionForProperty(PROPERTY_ID, saleTransactions);

        verify(mongoTemplate).updateFirst(
                query(where("_id").is(PROPERTY_ID)),
                new Update()
                        .pull("transactions", query(Criteria.where("_id").is(TRANSACTION_ID)))
                        .set("lastUpdated", LocalDate.now()),
                Property.class
        );

        verify(mongoTemplate).updateFirst(
                query(where("_id").is(PROPERTY_ID)),
                new Update()
                        .push("transactions").each(saleTransactions)
                        .set("lastUpdated", LocalDate.now()),
                Property.class
        );
    }


    @Test
    void addTransactionsUsesExpectedMatchingCriteria() {

        propertyUpdateRepository.addTransactionsForProperty(PROPERTY_ID, saleTransactions);

        verify(mongoTemplate).updateFirst(
                query(where("_id").is(PROPERTY_ID)
                        .and("transactions").not().elemMatch(Criteria.where("_id").is(TRANSACTION_ID))),
                new Update()
                        .push("transactions", saleTransaction)
                        .set("lastUpdated", LocalDate.now()),
                Property.class
        );
    }

}