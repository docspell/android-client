package org.docspell.docspellshare.data;

import org.docspell.docspellshare.util.Fun;
import org.docspell.docspellshare.util.Lazy;

import java.util.Objects;

public abstract class Option<A> {

  private Option() {}

  public static <A> Option<A> of(A value) {
    return value != null ? new Some<>(value) : empty();
  }

  public static <A> Option<A> ofNullable(A value) {
    return of(value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <A> Option<A> empty() {
    return (Option) None.INSTANCE;
  }

  public abstract <B> Option<B> map(Fun<A, B> f);

  public abstract <B> B fold(Fun<A, B> fa, Lazy<B> fe);

  public abstract Option<A> filter(Fun<A, Boolean> pred);

  public abstract boolean isPresent();

  public final boolean isEmpty() {
    return !isPresent();
  }

  public static final class Some<A> extends Option<A> {
    private final A value;

    public Some(A value) {
      this.value = value;
    }

    @Override
    public Option<A> filter(Fun<A, Boolean> pred) {
      if (Boolean.TRUE.equals(pred.apply(value))) {
        return this;
      } else {
        return empty();
      }
    }

    @Override
    public <B> Option<B> map(Fun<A, B> f) {
      return new Some<>(f.apply(value));
    }

    @Override
    public <B> B fold(Fun<A, B> fa, Lazy<B> fe) {
      return fa.apply(value);
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Some<?> some = (Some<?>) o;
      return Objects.equals(value, some.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
      return "Some{" + "value=" + value + '}';
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final class None extends Option<Void> {
    private static final Option<Void> INSTANCE = new None();

    @Override
    public <B> Option<B> map(Fun<Void, B> f) {
      return (Option) this;
    }

    @Override
    public <B> B fold(Fun<Void, B> fa, Lazy<B> fe) {
      return fe.get();
    }

    @Override
    public Option<Void> filter(Fun<Void, Boolean> pred) {
      return this;
    }

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public String toString() {
      return "None";
    }
  }
}
