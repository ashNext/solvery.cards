package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import solvery.cards.dto.UserRegistrationTo;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;
import solvery.cards.validator.user.UniqueUserMailValidator;
import solvery.cards.validator.user.UniqueUserUsernameValidator;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping(value = "/registration")
public class UserController {

  private final UserService service;

  private final UniqueUserMailValidator uniqueUserMailValidator;

  private final UniqueUserUsernameValidator uniqueUserUsernameValidator;

  public UserController(UserService service, UniqueUserMailValidator uniqueUserMailValidator, UniqueUserUsernameValidator uniqueUserUsernameValidator) {
    this.service = service;
    this.uniqueUserMailValidator = uniqueUserMailValidator;
    this.uniqueUserUsernameValidator = uniqueUserUsernameValidator;
  }

  @InitBinder
  private void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(uniqueUserMailValidator, uniqueUserUsernameValidator);
  }

  @GetMapping
  public String registration(Model model) {
    model.addAttribute("userRegistrationTo", new UserRegistrationTo());
    return "registration";
  }

  @PostMapping
  public String create(@Valid UserRegistrationTo userRegistrationTo, BindingResult bindingResult) {
    if (bindingResult.hasErrors()){
      return "registration";
    }

    User user = new User(
        userRegistrationTo.getUsername(),
        userRegistrationTo.getPassword(),
        userRegistrationTo.getFullName(),
        userRegistrationTo.getEmail(),
        Collections.singleton(Role.USER));

    service.create(user);
    return "redirect:/login";
  }
}
