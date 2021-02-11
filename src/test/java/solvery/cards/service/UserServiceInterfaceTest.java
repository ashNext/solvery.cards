package solvery.cards.service;

public interface UserServiceInterfaceTest {

  void create();

  void createShouldReturnDuplicateUsername();

  void createShouldReturnDuplicateEmail();

  void loadUserByUsername();

  void loadUserByUsernameShouldReturnNotFound();
}
