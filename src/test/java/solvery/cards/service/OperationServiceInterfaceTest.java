package solvery.cards.service;

public interface OperationServiceInterfaceTest {

  void create();

  void addMoney();

  void addMoneyShouldReturnBalanceOutRangeOnOverBalance();

  void withdrawMoney();

  void withdrawMoneyShouldReturnBalanceOutRangeOnLowerBalance();

  void transferMoney();

  void getByFilter();
}
