package guru.data.property.price.ingest.handler.model.input;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum InputAction {

  ADDITION("A"),
  CHANGE("C"),
  DELETE("D");

  private static final Map<String, InputAction> ENUM_MAP;

  String value;

  InputAction(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  static {
    Map<String, InputAction> map = new ConcurrentHashMap<>();
    for (InputAction instance : InputAction.values()) {
      map.put(instance.value().toUpperCase(), instance);
    }
    ENUM_MAP = Collections.unmodifiableMap(map);
  }

  public static InputAction get(String value) {
    return Optional.ofNullable(ENUM_MAP.get(value.toUpperCase()))
            .orElseThrow(() -> new RuntimeException(value));
  }
}
