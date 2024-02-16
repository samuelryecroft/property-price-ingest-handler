package guru.data.property.price.ingest.handler.mappper;


import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.repository.PostCodeLocation;
import org.springframework.stereotype.Component;

@Component
public class GeoLocationMapper {

    public GeoLocation postcodeLocationToGeoLocation(PostCodeLocation postCodeLocationToGeoLocation) {
        return GeoLocation.builder()
                .coordinates(new float[]{
                        postCodeLocationToGeoLocation.geLongitude(),
                        postCodeLocationToGeoLocation.getLatitude()
                })
                .type("Point")
                .build();
    }
}
