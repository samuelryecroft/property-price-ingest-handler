package guru.data.property.price.ingest.handler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCodeIoRepository  extends JpaRepository<PostCodeLocation, Long> {

    @Query(value =
            "SELECT  ST_X(location::geometry) as lattitude, ST_Y(location::geometry) as longitude   FROM postcodes WHERE postcode = ?1",
            nativeQuery = true)
    Optional<PostCodeLocation> getPostCodeLocation(String postCode);
}
