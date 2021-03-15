package solvery.cards.controller;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.dto.mapper.UserRegistrationMapper;
import solvery.cards.model.User;
import solvery.cards.service.UserService;
import solvery.cards.validator.user.UniqueUserMailValidator;
import solvery.cards.validator.user.UniqueUserUsernameValidator;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/registration")
public class UserController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final UserService userService;

  private final UniqueUserMailValidator uniqueUserMailValidator;

  private final UniqueUserUsernameValidator uniqueUserUsernameValidator;

  public UserController(UserService userService, UniqueUserMailValidator uniqueUserMailValidator,
                        UniqueUserUsernameValidator uniqueUserUsernameValidator) {
    this.userService = userService;
    this.uniqueUserMailValidator = uniqueUserMailValidator;
    this.uniqueUserUsernameValidator = uniqueUserUsernameValidator;
  }

  @InitBinder
  private void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(uniqueUserMailValidator, uniqueUserUsernameValidator);
  }

  @GetMapping
  public String registration(Model model) {
    logger.info("get registration page");
    model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
    return "registration";
  }

  @PostMapping
  public String create(@Valid UserRegistrationDTO userRegistrationDTO,
                       BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "registration";
    }

    User user = Mappers.getMapper(UserRegistrationMapper.class).toUser(userRegistrationDTO);

    logger.info("create user {}", user);
    userService.create(user);
    return "redirect:/login";
  }
}
