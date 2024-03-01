package guru.data.property.price.ingest.handler.mappper;


import guru.data.property.price.ingest.handler.entities.PostCodeEntity;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import org.springframework.stereotype.Component;

@Component
public class GeoLocationMapper {

  public GeoLocation postcodeLocationToGeoLocation(PostCodeEntity postCodeLocationToGeoLocation) {
    return GeoLocation.builder()
        .coordinates(new double[]{
            postCodeLocationToGeoLocation.geLongitude(),
            postCodeLocationToGeoLocation.getLatitude()
        })
        .type("Point")
        .build();
  }
}
