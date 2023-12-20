package guru.data.property.price.ingest.handler.model.property;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  private String primaryAddressObjectName;

  private String secondaryAddressObjectName;

  private String street;

  private String locality;

  private String town;

  private String district;

  private String county;

  private String postCode;

  private String source;

}
