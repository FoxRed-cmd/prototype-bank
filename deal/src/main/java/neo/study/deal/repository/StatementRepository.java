package neo.study.deal.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import neo.study.deal.entity.Statement;

public interface StatementRepository extends JpaRepository<Statement, UUID> {

}
