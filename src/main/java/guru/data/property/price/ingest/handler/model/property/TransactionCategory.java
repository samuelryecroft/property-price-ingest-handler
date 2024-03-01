package guru.data.property.price.ingest.handler.model.property;

public enum TransactionCategory {

  STANDARD_PRICE_PAID("A"),
  ADDITIONAL_PRICE_PAID("B");

  private final String value;

  TransactionCategory(String value) {
    this.value = value;
  }
}

