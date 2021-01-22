package solvery.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long>, JpaSpecificationExecutor<Operation> {

  @Query("SELECT o FROM Operation o WHERE o.card=:card AND o.dateTime>=:startDate AND o.dateTime<=:endDate ORDER BY o.dateTime desc")
  List<Operation> getByFilter(
      @Param("card") Card card,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT o FROM Operation o WHERE o.card=:card AND o.recipientCardNumb=:recipientCardNumb AND o.dateTime>=:startDate AND o.dateTime<=:endDate ORDER BY o.dateTime desc")
  List<Operation> getByFilterWithRecipientCardNumb(
      @Param("card") Card card,
      @Param("recipientCardNumb") String recipientCardNumb,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
