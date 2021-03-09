package solvery.cards.util.exception;

public class NotFoundOperationByIdException extends NotFoundException {

  public static final String CODE_NOT_FOUND_OPERATION_BY_ID = "operation.notFound";

  public NotFoundOperationByIdException() {
    super(CODE_NOT_FOUND_OPERATION_BY_ID);
  }
}
