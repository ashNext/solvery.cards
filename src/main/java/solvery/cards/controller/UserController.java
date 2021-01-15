package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping(value = "/registration")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public String registration(Model model) {
    model.addAttribute("user", new User());
    return "registration";
  }

  @PostMapping
  public String create(@Valid User user, BindingResult bindingResult) {
    if (bindingResult.hasErrors()){
      return "registration";
    }

    user.setRoles(Collections.singleton(Role.USER));
    user.setEnabled(true);
    service.create(user);
    return "redirect:/login";
  }
}
