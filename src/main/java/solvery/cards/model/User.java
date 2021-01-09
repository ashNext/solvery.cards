package solvery.cards.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String username;

  private String password;

  private String fullName;

  private String email;

  private boolean enabled;

  @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  public User() {
  }

  public User(Integer id, String username, String password, String fullName, String email) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.email = email;
  }

  public User(String username, String password, String fullName, String email) {
    this(null, username, password, fullName, email);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String name) {
    this.fullName = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRoles();
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return enabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return enabled;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
