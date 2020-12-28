package solvery.cards.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import solvery.cards.model.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {

  Optional<Operation> findFirstByCardNumbOrderByDateTimeDesc(String cardNumb);
}
