package solvery.cards.util.exception;

public class NotFoundCardByNumbException extends NotFoundException {

  public static final String CODE_NOT_FOUND_CARD_BY_NUMB = "card.numbNotFound";

  public NotFoundCardByNumbException(String arg) {
    super(CODE_NOT_FOUND_CARD_BY_NUMB, arg);
  }
}
