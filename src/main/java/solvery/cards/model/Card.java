package solvery.cards.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import solvery.cards.util.CardUtil;

import java.util.Objects;

@Entity
@Table(name = "cards", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"numb"}, name = "cards_unique_numb_idx")})
public class Card {

  @Id
  @SequenceGenerator(name = "cards_seq_gen", sequenceName = "cards_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cards_seq_gen")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @Column(name = "numb", nullable = false)
  @NotBlank
  @Size(min = CardUtil.MIX_LENGTH_NUMB, max = CardUtil.MAX_LENGTH_NUMB)
  private String numb;

  @Column(name = "balance", nullable = false)
  @NotNull
  @Range(min = CardUtil.MIN_BALANCE, max = CardUtil.MAX_BALANCE)
  private Integer balance;

  @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
  private boolean enabled;

  public Card() {
  }

  public Card(Integer id, User user, String numb, Integer balance, boolean enabled) {
    this.id = id;
    this.user = user;
    this.numb = numb;
    this.balance = balance;
    this.enabled = enabled;
  }

  public Card(Integer id, User user, String numb, Integer balance) {
    this(id, user, numb, balance, true);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Card card = (Card) o;
    return enabled == card.enabled &&
        Objects.equals(id, card.id) &&
        Objects.equals(user, card.user) &&
        Objects.equals(numb, card.numb) &&
        Objects.equals(balance, card.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, numb, balance, enabled);
  }

  @Override
  public String toString() {
    return "Card{" +
        "id=" + id +
        ", user=" + user +
        ", numb='" + numb + '\'' +
        ", balance=" + balance +
        ", enabled=" + enabled +
        '}';
  }
}
