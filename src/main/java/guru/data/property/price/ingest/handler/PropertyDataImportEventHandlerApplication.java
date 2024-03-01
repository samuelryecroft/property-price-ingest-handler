package guru.data.property.price.ingest.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan
public class PropertyDataImportEventHandlerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PropertyDataImportEventHandlerApplication.class, args);
  }

}
