package neo.study.deal.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import neo.study.deal.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

}
