package org.docspell.docspellshare.util;

@FunctionalInterface
public interface Fun<A, B> {

  B apply(A a);
}
