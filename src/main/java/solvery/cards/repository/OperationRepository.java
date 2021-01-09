package solvery.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvery.cards.model.Operation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Long> {

  Optional<Operation> findFirstByCardNumbOrderByDateTimeDesc(String cardNumb);

  @Query("SELECT o FROM Operation o WHERE o.cardNumb=:cardNumb AND o.dateTime>=:startDate AND o.dateTime<=:endDate")
  List<Operation> getByFilter(
          @Param("cardNumb") String cardNumb,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate);
}
