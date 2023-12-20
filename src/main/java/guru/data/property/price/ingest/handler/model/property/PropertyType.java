package guru.data.property.price.ingest.handler.model.property;

public enum PropertyType {
  DETACHED("D"),
  SEMI_DETACHED("S"),
  TERRACED("T"),
  FLAT_MAISONETTES("F"),
  OTHER("O");

  private String value;

  PropertyType(String value) {
    this.value = value;
  }
}
