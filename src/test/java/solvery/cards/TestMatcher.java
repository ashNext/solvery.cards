package solvery.cards;

import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMatcher<T> {

  private final BiConsumer<T, T> assertion;
  private final BiConsumer<Iterable<T>, Iterable<T>> iterableAssertion;

  private TestMatcher(BiConsumer<T, T> assertion, BiConsumer<Iterable<T>, Iterable<T>> iterableAssertion) {
    this.assertion = assertion;
    this.iterableAssertion = iterableAssertion;
  }

  public static <T> TestMatcher<T> usingAssertions(BiConsumer<T, T> assertion, BiConsumer<Iterable<T>, Iterable<T>> iterableAssertion) {
    return new TestMatcher<>(assertion, iterableAssertion);
  }

  public static <T> TestMatcher<T> usingEqualsAssertions() {
    return usingAssertions(
        (a, e) -> assertThat(a).isEqualTo(e),
        (a, e) -> assertThat(a).isEqualTo(e));
  }

  public static <T> TestMatcher<T> usingFieldsWithIgnoringAssertions(String... fieldsToIgnore) {
    return usingAssertions(
        (a, e) -> assertThat(a).usingRecursiveComparison()
            .ignoringFields(fieldsToIgnore)
            .isEqualTo(e),
        (a, e) -> assertThat(a)
            .usingElementComparatorIgnoringFields(fieldsToIgnore)
            .isEqualTo(e));
  }

  public void assertMatch(T actual, T expected) {
    assertion.accept(actual, expected);
  }

  @SafeVarargs
  public final void assertMatch(Iterable<T> actual, T... expected) {
    assertMatch(actual, List.of(expected));
  }

  public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
    iterableAssertion.accept(actual, expected);
  }
}
