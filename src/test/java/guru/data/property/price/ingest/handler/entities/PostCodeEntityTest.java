package guru.data.property.price.ingest.handler.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostCodeEntityTest {

  private static Long ID = 12345L;

  @Mock
  private Point<G2D> location;

  @Mock
  private G2D g2D;

  private PostCodeEntity postCodeEntity;

  @BeforeEach
  public void setup(){
    postCodeEntity = new PostCodeEntity();

    postCodeEntity.setLocation(location);
    postCodeEntity.setId(ID);
  }

  @Test
  void getLatitudeExtractsCorrectValue() {
    when(location.getPosition()).thenReturn(g2D);
    when(g2D.getLat()).thenReturn(1234D);

    assertThat(postCodeEntity.getLatitude()).isEqualTo(1234D);

  }

  @Test
  void getLongitudeExtractsCorrectValue() {
    when(location.getPosition()).thenReturn(g2D);
    when(g2D.getLon()).thenReturn(1234D);

    assertThat(postCodeEntity.geLongitude()).isEqualTo(1234D);

  }

}