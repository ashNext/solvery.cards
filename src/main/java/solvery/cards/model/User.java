package solvery.cards.model;

import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"}, name = "users_unique_username_idx"),
    @UniqueConstraint(columnNames = {"email"}, name = "users_unique_email_idx")
})
public class User implements UserDetails {

  @Id
  @SequenceGenerator(name = "users_seq_gen", sequenceName = "users_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
  private Integer id;

  @Column(name = "username", nullable = false)
  @NotBlank
  @Size(min = 2, max = 100)
  private String username;

  @Column(name = "password", nullable = false)
  @NotBlank
  @Size(min = 1, max = 100)
  private String password;

  @Column(name = "full_name", nullable = false)
  @NotBlank
  @Size(min = 2, max = 100)
  private String fullName;

  @Column(name = "email", nullable = false)
  @Email
  @NotBlank
  @Size(max = 100)
  private String email;

  @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
  private boolean enabled;

  @Column(name = "roles")
  @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      uniqueConstraints = {
          @UniqueConstraint(columnNames = {"user_id", "roles"}, name = "user_roles_unique_idx")})
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  public User() {
  }

  public User(Integer id, String username, String password, String fullName, String email,
              Set<Role> roles, boolean enabled) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.email = email;
    this.enabled = enabled;
    this.roles = roles;
  }

  public User(Integer id, String username, String password, String fullName, String email, Set<Role> roles) {
    this(id, username, password, fullName, email, roles, true);
  }

  public User(String username, String password, String fullName, String email, Set<Role> roles) {
    this(null, username, password, fullName, email, roles, true);
  }

  public User(Integer id, String username, String password, String fullName, String email) {
    this(id, username, password, fullName, email, null, true);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return enabled == user.enabled &&
        Objects.equals(id, user.id) &&
        Objects.equals(username, user.username) &&
        Objects.equals(password, user.password) &&
        Objects.equals(fullName, user.fullName) &&
        Objects.equals(email, user.email) &&
        Objects.equals(roles, user.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, fullName, email, enabled, roles);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", fullName='" + fullName + '\'' +
        ", email='" + email + '\'' +
        ", enabled=" + enabled +
        ", roles=" + roles +
        '}';
  }
}
