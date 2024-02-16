package guru.data.property.price.ingest.handler.serivce;

import guru.data.property.price.ingest.handler.mappper.GeoLocationMapper;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.repository.PostCodeIoRepository;
import guru.data.property.price.ingest.handler.repository.PostCodeLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCodeGeoCodingServiceTest {

    private final String LOOKUP_POSTCODE = "BS32 0AL";
    @Mock
    private PostCodeIoRepository postCodeIoRepository;

    @Mock
    private GeoLocationMapper geoLocationMapper;

    @Mock
    private PostCodeLocation postCodeLocation;

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
        when(postCodeIoRepository.getPostCodeLocation(LOOKUP_POSTCODE)).thenReturn(Optional.empty());

        Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode(LOOKUP_POSTCODE);

        assertThat(result).isEmpty();

        verify(postCodeIoRepository).getPostCodeLocation(LOOKUP_POSTCODE);
        verify(geoLocationMapper, never()).postcodeLocationToGeoLocation(any());
    }

    @Test
    void getGeoLocationFromPostCodeWhenLookupFound() {

        GeoLocation expectedResult = GeoLocation.builder()
                .coordinates(new float[] {51.545754f, -2.559503f})
                .type("Point")
        .build();

        when(postCodeIoRepository.getPostCodeLocation(LOOKUP_POSTCODE)).thenReturn(Optional.of(postCodeLocation));
        when(geoLocationMapper.postcodeLocationToGeoLocation(postCodeLocation)).thenReturn(expectedResult);

        Optional<GeoLocation> result = postCodeGeoCodingService.getGeoLocationFromPostCode(LOOKUP_POSTCODE);

        assertThat(result).contains(expectedResult);


    }
}