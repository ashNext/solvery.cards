package solvery.cards.dto.mapper;

import org.mapstruct.Mapper;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.model.Role;
import solvery.cards.model.User;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class UserRegistrationMapper {

  public User toUser(UserRegistrationDTO userRegistrationDTO) {
    if (userRegistrationDTO == null) {
      return null;
    }

    Set<Role> roles = new HashSet<>();
    roles.add(Role.USER);
    if (userRegistrationDTO.isAdvanced()) {
      roles.add(Role.USER_ADVANCED);
    }

    return new User(
        userRegistrationDTO.getUsername(),
        userRegistrationDTO.getPassword(),
        userRegistrationDTO.getFullName(),
        userRegistrationDTO.getEmail(),
        roles);
  }

  public abstract UserRegistrationDTO toUserDTO(User user);
}
