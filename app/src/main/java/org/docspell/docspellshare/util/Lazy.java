package org.docspell.docspellshare.util;

@FunctionalInterface
public interface Lazy<A> {

  A get();
}
