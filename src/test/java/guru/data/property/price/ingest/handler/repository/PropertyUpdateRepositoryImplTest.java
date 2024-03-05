package guru.data.property.price.ingest.handler.repository;

import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LAST_UPDATED_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LATEST_DATA_DATE_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.LOCATION_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.PROPERTY_ID_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.PROPERTY_TYPE_FIELD;
import static guru.data.property.price.ingest.handler.constants.DatabaseFields.TRANSACTION_FIELD;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.model.property.Property;
import guru.data.property.price.ingest.handler.model.property.PropertyType;
import guru.data.property.price.ingest.handler.model.property.SaleTransaction;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class PropertyUpdateRepositoryImplTest {

  private final String PROPERTY_ID = "PROPERTY_ID";
  private final String TRANSACTION_ID = "TRANSACTION_ID";

  private final LocalDate LATEST_DATA_DATE = LocalDate.now();

  private final SaleTransaction saleTransaction = SaleTransaction.builder().id("TRANSACTION_ID")
      .build();
  private final Set<SaleTransaction> saleTransactions = Set.of(
      saleTransaction
  );

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private GeoLocation geoLocation;


  @Mock
  private Property property;

  @InjectMocks
  private PropertyUpdateRepositoryImpl propertyUpdateRepository;


  @Test
  void removeTransactionsUsesExpectedMatchingCriteria() {

    propertyUpdateRepository.removeTransactionsForProperty(PROPERTY_ID, saleTransactions, LATEST_DATA_DATE);

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)),
        new Update()
            .pull(TRANSACTION_FIELD, query(Criteria.where("_id").is(TRANSACTION_ID)))
            .set(LAST_UPDATED_FIELD, LocalDate.now())
            .set(LATEST_DATA_DATE_FIELD, LATEST_DATA_DATE),
        Property.class
    );
  }

  @Test
  void updateTransactionsUsesExpectedMatchingCriteria() {

    propertyUpdateRepository.updateTransactionForProperty(PROPERTY_ID, saleTransactions, LATEST_DATA_DATE);

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)),
        new Update()
            .pull(TRANSACTION_FIELD, query(Criteria.where("_id").is(TRANSACTION_ID)))
            .set(LAST_UPDATED_FIELD, LocalDate.now())
            .set(LATEST_DATA_DATE_FIELD, LATEST_DATA_DATE),
        Property.class
    );

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)),
        new Update()
            .push(TRANSACTION_FIELD).each(saleTransactions)
            .set(LAST_UPDATED_FIELD, LocalDate.now())
            .set(LATEST_DATA_DATE_FIELD, LATEST_DATA_DATE),
        Property.class
    );
  }


  @Test
  void addTransactionsUsesExpectedMatchingCriteria() {

    propertyUpdateRepository.addTransactionsForProperty(PROPERTY_ID, saleTransactions, LATEST_DATA_DATE);

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)
            .and(TRANSACTION_FIELD).not().elemMatch(Criteria.where("_id").is(TRANSACTION_ID))),
        new Update()
            .push(TRANSACTION_FIELD, saleTransaction)
            .set(LAST_UPDATED_FIELD, LocalDate.now())
            .set(LATEST_DATA_DATE_FIELD, LATEST_DATA_DATE),
        Property.class
    );
  }

  @Test
  void updatePropertyDetailsUseExpectedMatchingCriteria() {

    when(property.getId()).thenReturn(PROPERTY_ID);
    when(property.getLatestTransactionDate()).thenReturn(LATEST_DATA_DATE);
    when(property.getPropertyType()).thenReturn(PropertyType.DETACHED);

    propertyUpdateRepository.updatePropertyDetails(property);

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)
            .and(LATEST_DATA_DATE_FIELD).lte(LATEST_DATA_DATE)),
        new Update()
            .set(PROPERTY_TYPE_FIELD, PropertyType.DETACHED)
            .set(LATEST_DATA_DATE_FIELD, LATEST_DATA_DATE),
        Property.class
    );
  }

  @Test
  void updatePropertyLocationUsesExpectedMatchingCriteria() {

    when(property.getId()).thenReturn(PROPERTY_ID);
    when(property.getLatestTransactionDate()).thenReturn(LATEST_DATA_DATE);

    propertyUpdateRepository.updatePropertyLocation(property, geoLocation);

    verify(mongoTemplate).updateFirst(
        query(where(PROPERTY_ID_FIELD).is(PROPERTY_ID)
            .and(LATEST_DATA_DATE_FIELD).lte(LATEST_DATA_DATE)),
        new Update()
            .set(LOCATION_FIELD, geoLocation),
        Property.class
    );
  }

}