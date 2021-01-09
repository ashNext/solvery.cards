package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;

import java.util.Collections;

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

    if (service.getByUsername(user.getUsername()) != null) {
      return "registration";
    }

    user.setRoles(Collections.singleton(Role.USER));
    user.setEnabled(true);
    service.create(user);
    return "redirect:/login";
  }
}
