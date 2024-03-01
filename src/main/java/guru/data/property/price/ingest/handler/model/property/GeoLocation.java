package guru.data.property.price.ingest.handler.model.property;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GeoLocation {

  String type;
  double[] coordinates;
}
