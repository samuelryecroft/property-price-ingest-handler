package guru.data.property.price.ingest.handler.serivce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.data.property.price.ingest.handler.entities.PostCodeEntity;
import guru.data.property.price.ingest.handler.mappper.GeoLocationMapper;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.repository.PostCodeIoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostCodeGeoCodingServiceTest {

  private final String LOOKUP_POSTCODE = "BS32 0AL";
  @Mock
  private PostCodeIoRepository postCodeIoRepository;

  @Mock
  private GeoLocationMapper geoLocationMapper;

  @Mock
  private PostCodeEntity postCodeLocation;

  @InjectMocks
  private PostCodeGeoCodingService postCodeGeoCodingService;

  @Test
  void getGeoLocationFromPostCodeWithNullValueReturnsEmptyOptional() {

    Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode(null);

    assertThat(result).isEmpty();
  }

  @Test
  void getGeoLocationFromPostCodeWithEmptyValueReturnsEmptyOptional() {
    Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode("");

    assertThat(result).isEmpty();
  }

  @Test
  void getGeoLocationFromPostCodeWhenNoLookupFoundInDatabaseReturnsEmptyOptional() {
    when(postCodeIoRepository.getPostCodeEntityByPostcode(LOOKUP_POSTCODE)).thenReturn(
        Optional.empty());

    Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode(
        LOOKUP_POSTCODE);

    assertThat(result).isEmpty();

    verify(postCodeIoRepository).getPostCodeEntityByPostcode(LOOKUP_POSTCODE);
    verify(geoLocationMapper, never()).postcodeLocationToGeoLocation(any());
  }

  @Test
  void getGeoLocationFromPostCodeWhenLookupFound() {

    GeoLocation expectedResult = GeoLocation.builder()
        .coordinates(new double[]{51.545754d, -2.559503d})
        .type("Point")
        .build();

    when(postCodeIoRepository.getPostCodeEntityByPostcode(LOOKUP_POSTCODE)).thenReturn(
        Optional.of(postCodeLocation));
    when(geoLocationMapper.postcodeLocationToGeoLocation(postCodeLocation)).thenReturn(
        expectedResult);

    Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode(
        LOOKUP_POSTCODE);

    assertThat(result).contains(expectedResult);


  }
}