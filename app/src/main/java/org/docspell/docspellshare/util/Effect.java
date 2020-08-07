package org.docspell.docspellshare.util;

@FunctionalInterface
public interface Effect<A> {

  void run(A value);
}
