package guru.data.property.price.ingest.handler.model.property;

public enum Tenure {

  FREEHOLD("F"),
  LEASEHOLD("L"),

  UNKNOWN("U");

  private String value;

  Tenure(String value) {
    this.value = value;
  }
}
