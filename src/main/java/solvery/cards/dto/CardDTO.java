package solvery.cards.dto;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import solvery.cards.util.CardUtil;
import solvery.cards.validator.card.UniqueCardNumber;

public class CardDTO {

  private Integer id;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = CardUtil.MIX_LENGTH_NUMB, max = CardUtil.MAX_LENGTH_NUMB, message = "{card.numbSize}")
  @UniqueCardNumber(message = "{card.uniqueCardNumber}")
  private String numb;

  public CardDTO() {
  }

  public CardDTO(Integer id, String numb) {
    this.id = id;
    this.numb = numb;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNumb() {
    return numb;
  }

  public void setNumb(String numb) {
    this.numb = numb;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardDTO cardDTO = (CardDTO) o;
    return Objects.equals(id, cardDTO.id) &&
        Objects.equals(numb, cardDTO.numb);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, numb);
  }

  @Override
  public String toString() {
    return "CardTo{" +
        "id=" + id +
        ", numb='" + numb + '\'' +
        '}';
  }
}
