package solvery.cards.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "cards", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"numb"}, name = "cards_unique_numb_idx")})
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @Column(name = "numb", nullable = false)
  @NotBlank
  @Size(min = 2, max = 16)
  private String numb;

  @Column(name = "balance", nullable = false)
  @NotNull
  @Range(min = 0, max = 999999999)
  private Integer balance;

  @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
  private boolean enabled;

  public Card() {
  }

  public Card(Integer id, User user, String numb, Integer balance) {
    this.id = id;
    this.user = user;
    this.numb = numb;
    this.balance = balance;
  }

  public Card(User user, String numb, Integer balance) {
    this(null, user, numb, balance);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getNumb() {
    return numb;
  }

  public void setNumb(String numb) {
    this.numb = numb;
  }

  public Integer getBalance() {
    return balance;
  }

  public void setBalance(Integer balance) {
    this.balance = balance;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
