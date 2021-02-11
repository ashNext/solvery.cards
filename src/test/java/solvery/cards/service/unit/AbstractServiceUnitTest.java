package solvery.cards.service.unit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
public abstract class AbstractServiceUnitTest {

  protected final static User exceptedUser =
      new User(1, "u1", "@@", "user1", "a@b.ru",
          Collections.singleton(Role.USER), true);

  protected final static Card exceptedCard = new Card(1, exceptedUser, "11", 0, true);

  @Mock
  protected MessageSourceAccessor messageSourceAccessor;
}
