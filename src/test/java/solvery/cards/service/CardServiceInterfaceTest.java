package solvery.cards.service;

public interface CardServiceInterfaceTest {

  void getAllByUser();

  void getAllByUserAdvanced();

  void getAllEnabledByUser();

  void getAllEnabledByUserAdvanced();

  void create();

  void createShouldReturnDuplicateNumber();

  void close();

  void getEnabledById();

  void getEnabledByIdShouldReturnNotFound();

  void getEnabledByCardNumb();

  void getEnabledByCardNumbShouldReturnNotFound();

  void openBack();

  void getDisabledById();

  void getDisabledByIdShouldReturnNotFound();
}
