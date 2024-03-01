package guru.data.property.price.ingest.handler.repository;

import guru.data.property.price.ingest.handler.entities.PostCodeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCodeIoRepository extends JpaRepository<PostCodeEntity, Long> {

  Optional<PostCodeEntity> getPostCodeEntityByPostcode(String postCode);
}
