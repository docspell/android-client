package org.docspell.docspellshare.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.docspell.docspellshare.data.Option;
import org.docspell.docspellshare.data.UrlItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DataStore {
  private static final String SHARED_ITEMS_KEY = "org.docspell.docspellshare.ITEMS_KEY";
  private static final String SHARED_DEFAULT_KEY = "org.docspell.docspellshare.DEFAULT_KEY";
  private final Activity activity;

  enum Setting {
    DEFAULT_ITEM
  }

  public DataStore(Activity activity) {
    this.activity = activity;
  }

  public List<UrlItem> loadAll() {
    SharedPreferences prefs = getItemPreferences();
    List<UrlItem> result = new ArrayList<>();
    for (String key : prefs.getAll().keySet()) {
      result.add(new UrlItem(key, prefs.getString(key, "")));
    }
    Collections.sort(result);
    return result;
  }

  public void remove(String name) {
    SharedPreferences prefs = getItemPreferences();
    SharedPreferences.Editor edit = prefs.edit();
    edit.remove(name);
    edit.apply();
  }

  public void addOrReplace(UrlItem item) {
    SharedPreferences prefs = getItemPreferences();
    SharedPreferences.Editor edit = prefs.edit();
    edit.putString(item.getName(), item.getUrl());
    edit.apply();
  }

  public void removeDefault() {
    SharedPreferences prefs = getDefaultPreferences();
    SharedPreferences.Editor edit = prefs.edit();
    edit.remove(Setting.DEFAULT_ITEM.name());
    edit.apply();
  }

  public void setDefault(String name) {
    SharedPreferences prefs = getDefaultPreferences();
    SharedPreferences.Editor edit = prefs.edit();
    edit.putString(Setting.DEFAULT_ITEM.name(), name);
    edit.apply();
  }

  public Option<String> getDefault() {
    SharedPreferences prefs = getDefaultPreferences();
    String res = prefs.getString(Setting.DEFAULT_ITEM.name(), null);
    return Option.ofNullable(res).filter(Strings::notNullOrBlank);
  }

  public boolean isDefault(String name) {
    return getDefault().equals(Option.of(name));
  }

  private SharedPreferences getDefaultPreferences() {
    return activity.getSharedPreferences(SHARED_DEFAULT_KEY, Context.MODE_PRIVATE);
  }

  private SharedPreferences getItemPreferences() {
    return activity.getSharedPreferences(SHARED_ITEMS_KEY, Context.MODE_PRIVATE);
  }
}
