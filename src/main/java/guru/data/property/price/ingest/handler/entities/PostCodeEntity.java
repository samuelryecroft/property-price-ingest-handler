package guru.data.property.price.ingest.handler.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

@Entity
@Table(name = "postcodes")
@Data
public class PostCodeEntity {

  @Id
  long id;

  Point<G2D> location;

  String postcode;

  public double getLatitude() {
    return this.location.getPosition().getLat();
  }

  public double geLongitude() {
    return this.location.getPosition().getLon();
  }

}
