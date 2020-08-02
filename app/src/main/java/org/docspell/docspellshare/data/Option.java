package org.docspell.docspellshare.data;

import org.docspell.docspellshare.Fun;
import org.docspell.docspellshare.Lazy;

public abstract class Option<A> {

  private Option() {}

  public static <A> Option<A> of(A value) {
    return new Some<>(value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <A> Option<A> empty() {
    return (Option) None.INSTANCE;
  }

  public abstract <B> Option<B> map(Fun<A, B> f);

  public abstract <B> B fold(Fun<A, B> fa, Lazy<B> fe);

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
    public boolean isPresent() {
      return false;
    }
  }
}
