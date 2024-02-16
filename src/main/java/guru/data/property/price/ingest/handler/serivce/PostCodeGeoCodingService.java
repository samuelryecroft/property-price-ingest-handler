package guru.data.property.price.ingest.handler.serivce;

import guru.data.property.price.ingest.handler.mappper.GeoLocationMapper;
import guru.data.property.price.ingest.handler.model.property.GeoLocation;
import guru.data.property.price.ingest.handler.repository.PostCodeIoRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * A service for geocoding postcodes.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PostCodeGeoCodingService {

  private final PostCodeIoRepository postCodeIoRepository;
  private final GeoLocationMapper geoLocationMapper;

  /**
   * Produces an optional GeoLocation for a given location.
   *
   * @param postCode The postcode to lookup.
   * @return The GeoLocation if found.
   */
  public Optional<GeoLocation> getGeoLocationFromPostCode(String postCode) {

    if (Objects.isNull(postCode) || postCode.isEmpty()) {
      log.warn("Unable to lookup postCode {}", postCode);
      return Optional.empty();
    }

    return postCodeIoRepository.getPostCodeLocation(postCode)
        .map(geoLocationMapper::postcodeLocationToGeoLocation);

  }
}
