package solvery.cards.service;

public interface CardServiceInterfaceTest {

  void getAllEnabledByUser();

  void create();

  void createShouldReturnDuplicateNumber();

  void close();

  void getEnabledById();

  void getEnabledByIdShouldReturnNotFound();

  void getEnabledByCardNumb();

  void getEnabledByCardNumbShouldReturnNotFound();
}
