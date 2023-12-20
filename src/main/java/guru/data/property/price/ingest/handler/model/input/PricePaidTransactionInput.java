package guru.data.property.price.ingest.handler.model.input;

import guru.data.property.price.ingest.handler.model.property.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PricePaidTransactionInput {
  private InputAction inputAction;
  private Property property;
}
