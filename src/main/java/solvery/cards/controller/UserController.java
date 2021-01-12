package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;

import java.util.Collections;

@Controller
@RequestMapping(value = "/registration")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public String registration() {
    return "registration";
  }

  @PostMapping
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
