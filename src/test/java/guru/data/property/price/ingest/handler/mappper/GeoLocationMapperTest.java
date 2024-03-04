package guru.data.property.price.ingest.handler.mappper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import guru.data.property.price.ingest.handler.entities.PostCodeEntity;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeoLocationMapperTest {
  private static final Double LAT = 123D;
  private static final Double LONG = 321D;

  @Mock
  private PostCodeEntity postCodeEntity;

  @InjectMocks
  private GeoLocationMapper geoLocationMapper;

  @Test
  void postcodeLocationToGeoLocationMapsCorrectFields() {
    when(postCodeEntity.getLatitude()).thenReturn(LAT);
    when(postCodeEntity.geLongitude()).thenReturn(LONG);

    final GeoLocation geoLocation = geoLocationMapper.postcodeLocationToGeoLocation(postCodeEntity);

    assertThat(geoLocation)
        .extracting("type", "coordinates")
        .contains("Point", new double[] {LONG, LAT});

  }

}