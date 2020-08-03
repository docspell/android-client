package org.docspell.docspellshare.util;

import org.docspell.docspellshare.data.Option;

import java.net.MalformedURLException;
import java.net.URL;

public final class Strings {
  private Strings() {}

  public static boolean isNullOrBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  public static boolean notNullOrBlank(String s) {
    return !isNullOrBlank(s);
  }

  public static String requireNonEmpty(String s, String msg) {
    if (isNullOrBlank(s)) {
      throw new IllegalArgumentException(msg);
    } else {
      return s;
    }
  }

  public static boolean isValidUrl(String s) {
    return notNullOrBlank(s) && makeUrl(s).isPresent();
  }

  private static Option<URL> makeUrl(String s) {
    try {
      return Option.of(new URL(s));
    } catch (MalformedURLException e) {
      return Option.empty();
    }
  }
}
