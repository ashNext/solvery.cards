package solvery.cards.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import solvery.cards.validator.FieldsValueMatch;

@FieldsValueMatch.List({
    @FieldsValueMatch(
        field = "confirmPassword",
        fieldMatch = "password",
        message = "{user.matchRetypePassword}"
    )
})
public class UserRegistrationTo {

  private Integer id;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = 2, max = 100, message = "{user.userNameSize}")
  private String username;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = 1, max = 100, message = "{user.passwordSize}")
  private String password;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = 1, max = 100, message = "{user.retypePasswordSize}")
  private String confirmPassword;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = 2, max = 100, message = "{user.fullNameSize}")
  private String fullName;

  @Email(message = "{user.emailFormat}")
  @NotBlank(message = "{common.notBlank}")
  @Size(max = 100, message = "{user.emailSize}")
  private String email;

  public UserRegistrationTo() {
  }

  public UserRegistrationTo(Integer id,
      @NotBlank @Size(min = 2, max = 100) String username,
      @NotBlank @Size(min = 1, max = 100) String password,
      @NotBlank @Size(min = 1, max = 100) String confirmPassword,
      @NotBlank @Size(min = 2, max = 100) String fullName,
      @Email @NotBlank @Size(max = 100) String email) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.confirmPassword = confirmPassword;
    this.fullName = fullName;
    this.email = email;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
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

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
