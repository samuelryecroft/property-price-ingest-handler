package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.model.property.Property;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyRepository extends MongoRepository<Property, String>, PropertyUpdateRepository{

}
