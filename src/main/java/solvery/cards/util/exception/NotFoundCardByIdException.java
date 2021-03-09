package solvery.cards.util.exception;

public class NotFoundCardByIdException extends NotFoundException {

  public static final String CODE_NOT_FOUND_CARD_BY_ID = "card.cardByIdNotFound";

  public NotFoundCardByIdException() {
    super(CODE_NOT_FOUND_CARD_BY_ID);
  }
}
