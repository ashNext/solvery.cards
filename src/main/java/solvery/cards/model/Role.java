package solvery.cards.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  USER,
  USER_ADVANCED;

  @Override
  public String getAuthority() {
    return name();
  }
}
