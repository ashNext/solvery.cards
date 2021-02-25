package solvery.cards;

import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.model.Role;
import solvery.cards.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class OperationTestData {

  public final static User USER1 =
      new User(1, "u1", "1", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);

  public static final Card CARD1 = new Card(1, USER1, "11", 6500, true);
  public static final Card CARD2 = new Card(2, USER1, "12", 2000, true);
  public static final Card CARD3 = new Card(3, USER1, "13", 999999000, true);

  public static final String CARD_NUMB_NOT_FOUND = "42";

  public static final int SUM_OVER_BALANCE_CARD1 = 999999000;
  public static final int SUM_LOWER_BALANCE_CARD1 = 7000;

  public static final long OPERATION_NOT_FOUND_ID = 42;
  public static final long OPERATION1_NOT_CREATED_ID = 14;
  public static final long OPERATION2_NOT_CREATED_ID = 15;

  public static final Operation OPERATION_DEPOSIT_CARD1 =
      new Operation(14L, CARD1, null, 1000, 7500, null);
  public static final Operation OPERATION_WITHDRAW_CARD1 =
      new Operation(14L, CARD1, null, -1000, 5500, null);

  public static final Operation OPERATION_TRANSFER_FROM_CARD1 =
      new Operation(14L, CARD1, "12", -1000, 5500, null);
  public static final Operation OPERATION_TRANSFER_TO_CARD2 =
      new Operation(15L, CARD2, "11", 1000, 3000, null);


  public static final Operation OPERATION1_CARD1 = new Operation(1L, CARD1, null, 10000, 10000,
      LocalDateTime.of(2021, 1, 20, 10, 0, 0));
  public static final Operation OPERATION2_CARD1 = new Operation(2L, CARD1, null, -5000, 5000,
      LocalDateTime.of(2021, 1, 21, 20, 0, 0));
  public static final Operation OPERATION3_CARD1 = new Operation(3L, CARD1, "12", -2500, 2500,
      LocalDateTime.of(2021, 1, 21, 21, 0, 0));
  public static final Operation OPERATION4_CARD2 = new Operation(4L, CARD2, "11", 2500, 2500,
      LocalDateTime.of(2021, 1, 21, 21, 0, 0));
  public static final Operation OPERATION5_CARD1 = new Operation(5L, CARD1, null, 10000, 12500,
      LocalDateTime.of(2021, 1, 22, 10, 0, 0));
  public static final Operation OPERATION6_CARD1 = new Operation(6L, CARD1, null, -5000, 7500,
      LocalDateTime.of(2021, 1, 22, 12, 0, 0));
  public static final Operation OPERATION7_CARD1 = new Operation(7L, CARD1, "13", -2000, 5500,
      LocalDateTime.of(2021, 1, 23, 15, 0, 0));
  public static final Operation OPERATION8_CARD3 = new Operation(8L, CARD3, "11", 2000, 2000,
      LocalDateTime.of(2021, 1, 23, 15, 0, 0));
  public static final Operation OPERATION9_CARD2 = new Operation(9L, CARD2, null, 2000, 4500,
      LocalDateTime.of(2021, 1, 24, 9, 0, 0));
  public static final Operation OPERATION10_CARD2 = new Operation(10L, CARD2, null, -1500, 3000,
      LocalDateTime.of(2021, 1, 24, 16, 0, 0));
  public static final Operation OPERATION11_CARD2 = new Operation(11L, CARD2, "11", -1000, 2000,
      LocalDateTime.of(2021, 1, 25, 13, 0, 0));
  public static final Operation OPERATION12_CARD1 = new Operation(12L, CARD1, "12", 1000, 6500,
      LocalDateTime.of(2021, 1, 25, 13, 0, 0));
  public static final Operation OPERATION13_CARD3 = new Operation(13L, CARD3, null, 999997000, 999999000,
      LocalDateTime.of(2021, 1, 25, 17, 0, 0));

  public static final List<Operation> OPERATIONS_CARD1 =
      List.of(OPERATION12_CARD1, OPERATION7_CARD1, OPERATION6_CARD1, OPERATION5_CARD1,
          OPERATION3_CARD1, OPERATION2_CARD1, OPERATION1_CARD1);
}
