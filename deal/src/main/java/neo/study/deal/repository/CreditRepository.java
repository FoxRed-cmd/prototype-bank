package neo.study.deal.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import neo.study.deal.entity.Credit;

public interface CreditRepository extends JpaRepository<Credit, UUID> {

}
