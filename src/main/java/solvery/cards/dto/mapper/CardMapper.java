package solvery.cards.dto.mapper;

import org.mapstruct.Mapper;
import solvery.cards.dto.CardDTO;
import solvery.cards.model.Card;
import solvery.cards.model.User;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

  public Card toCard(CardDTO cardDTO, User user) {
    if (cardDTO == null || user == null) {
      return null;
    }

    return new Card(user, cardDTO.getNumb());
  }

  public abstract CardDTO toCardDTO(Card card);
}
