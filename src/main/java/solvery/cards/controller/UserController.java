package solvery.cards.controller;

import java.util.Collections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import solvery.cards.model.User;
import solvery.cards.service.UserService;

@Controller
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/registration")
  public String registration() {
    return "registration";
  }

  @PostMapping("/registration")
  public String create(User user) {

    if (service.getByLogin(user.getLogin()) != null) {
      return "registration";
    }

    user.setRoles(Collections.singleton(Role.USER));
    user.setActive(true);
    service.create(user);
    return "redirect:/login";
  }
}
