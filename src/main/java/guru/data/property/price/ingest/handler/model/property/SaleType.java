package guru.data.property.price.ingest.handler.model.property;

public enum SaleType {

  NEW("Y"),
  EXISTING("N");

  private String value;

  SaleType(String value) {
    this.value = value;
  }
}