package org.docspell.docspellshare.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;

public final class Uris {

  private Uris() {}

  public static long getFileSize(Uri uri, ContentResolver resolver) {
    long size = getFileSizeViaFile(uri);
    if (size == -1) {
      size = getFileSizeViaCursor(uri, resolver);
    }
    return size;
  }

  private static long getFileSizeViaCursor(Uri uri, ContentResolver resolver) {
    try {
      Cursor cursor = resolver.query(uri, null, null, null, null);
      if (cursor != null) {
        try {
          cursor.moveToFirst();
          int index = cursor.getColumnIndex(OpenableColumns.SIZE);
          return cursor.getLong(index);
        } finally {
          cursor.close();
        }
      }
      return -1;
    } catch (Exception e) {
      Log.e("filesize", "Error getting filesize via cursor", e);
      return -1;
    }
  }

  private static long getFileSizeViaFile(Uri uri) {
    try {
      if (uri.getPath() != null) {
        File file = new File(uri.getPath());
        if (file.exists()) {
          return file.length();
        }
      }
      return -1;
    } catch (Exception e) {
      Log.e("filesize", "Error getting size via file", e);
      return -1;
    }
  }
}
