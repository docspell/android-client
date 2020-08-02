package org.docspell.docspellshare.data;

import java.io.Serializable;
import java.util.Objects;
import org.docspell.docspellshare.Strings;

public final class UrlItem implements Serializable, Comparable<UrlItem> {

  private final String name;
  private final String url;

  public UrlItem(String name, String url) {
    this.name = Strings.requireNonEmpty(name, "name must not be null");
    this.url = Strings.requireNonEmpty(url, "url must not be null");
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public int compareTo(UrlItem urlItem) {
    return name.compareTo(urlItem.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UrlItem urlItem = (UrlItem) o;
    return Objects.equals(name, urlItem.name) && Objects.equals(url, urlItem.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, url);
  }

  @Override
  public String toString() {
    return "UrlItem{" + "name='" + name + '\'' + ", url='" + url + '\'' + '}';
  }
}
