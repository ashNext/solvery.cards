package solvery.cards.dto;

import solvery.cards.validator.FieldsValueMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.EXCEPTION_NO_MATCH_RETYPE_PASSWORD;

@FieldsValueMatch.List({
    @FieldsValueMatch(
        field = "confirmPassword",
        fieldMatch = "password",
        message = "{" + EXCEPTION_NO_MATCH_RETYPE_PASSWORD + "}"
    )
})
public class UserRegistrationDTO {

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

  private boolean advanced;

  public UserRegistrationDTO() {
  }

  public UserRegistrationDTO(Integer id,
                             @NotBlank @Size(min = 2, max = 100) String username,
                             @NotBlank @Size(min = 1, max = 100) String password,
                             @NotBlank @Size(min = 1, max = 100) String confirmPassword,
                             @NotBlank @Size(min = 2, max = 100) String fullName,
                             @Email @NotBlank @Size(max = 100) String email,
                             boolean advanced) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.confirmPassword = confirmPassword;
    this.fullName = fullName;
    this.email = email;
    this.advanced = advanced;
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

  public boolean isAdvanced() {
    return advanced;
  }

  public void setAdvanced(boolean advanced) {
    this.advanced = advanced;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRegistrationDTO that = (UserRegistrationDTO) o;
    return advanced == that.advanced &&
        Objects.equals(id, that.id) &&
        Objects.equals(username, that.username) &&
        Objects.equals(password, that.password) &&
        Objects.equals(confirmPassword, that.confirmPassword) &&
        Objects.equals(fullName, that.fullName) &&
        Objects.equals(email, that.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, confirmPassword, fullName, email, advanced);
  }

  @Override
  public String toString() {
    return "UserRegistrationDTO{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", confirmPassword='" + confirmPassword + '\'' +
        ", fullName='" + fullName + '\'' +
        ", email='" + email + '\'' +
        ", advanced=" + advanced +
        '}';
  }
}
