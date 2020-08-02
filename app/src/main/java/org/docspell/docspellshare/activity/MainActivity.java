package org.docspell.docspellshare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.UrlItem;

public class MainActivity extends AppCompatActivity {
  private static final int URL_ITEM_RESULT = 1;
  private static final String SHARED_PREF_KEY = "org.docspell.docspellshare.ITEMS_KEY";

  private SwipeRefreshLayout swipeView;
  private RecyclerView urlListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    urlListView = findViewById(R.id.urlListView);
    urlListView.setLayoutManager(new LinearLayoutManager(this));
    urlListView.setAdapter(new UrlItemAdapter(loadData()));

    swipeView = findViewById(R.id.swipeCnt);
    swipeView.setOnRefreshListener(
        () -> {
          UrlItemAdapter ua = (UrlItemAdapter) urlListView.getAdapter();
          if (ua != null) {
            ua.replaceAll(loadData());
          }
          swipeView.setRefreshing(false);
        });
  }

  private List<UrlItem> loadData() {
    SharedPreferences prefs = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    List<UrlItem> result = new ArrayList<>();
    for (String key : prefs.getAll().keySet()) {
      result.add(new UrlItem(key, prefs.getString(key, "")));
    }
    Collections.sort(result);
    return result;
  }

  public void addNewUrl(View view) {
    Intent intent = new Intent(this, AddUrlActivity.class);
    startActivityForResult(intent, URL_ITEM_RESULT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data != null && requestCode == URL_ITEM_RESULT && resultCode == RESULT_OK) {
      UrlItem item = (UrlItem) data.getSerializableExtra(UrlItem.class.getName());
      Log.i("add url", "item is " + item);
      if (item != null) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(item.getName(), item.getUrl());
        edit.apply();

        UrlItemAdapter ua = (UrlItemAdapter) urlListView.getAdapter();
        if (ua != null) {
          ua.add(item);
        }
      }
    }
  }

  static class UrlItemAdapter extends RecyclerView.Adapter<UrlItemHolder> {
    private final Map<String, UrlItem> items;

    public UrlItemAdapter(List<UrlItem> items) {
      this.items = new HashMap<>();
      for (UrlItem item : items) {
        this.items.put(item.getName(), item);
      }
    }

    @NonNull
    @Override
    public UrlItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.url_item_layout, parent, false);
      UrlItemHolder holder = new UrlItemHolder(v);
      ImageButton deleteBtn = v.findViewById(R.id.deleteItemBtn);
      deleteBtn.setOnClickListener(
          view -> {
            try {
              UrlItem item = holder.item;
              if (item != null) {
                items.remove(item.getName());
                notifyDataSetChanged();
                SharedPreferences prefs =
                    parent.getContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.remove(item.getName());
                edit.apply();
              }
            } catch (Exception e) {
              Log.e("remove-item", "Cannot remove item from position: " + holder.position);
            }
          });
      v.setOnClickListener(view -> {

      });
      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UrlItemHolder holder, int position) {
      ArrayList<String> names = new ArrayList<>(items.keySet());
      Collections.sort(names);
      String name = names.get(position);
      UrlItem item = items.get(name);
      if (item != null) {
        holder.setContent(item, position);
      }
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    public void replaceAll(List<UrlItem> newData) {
      items.clear();
      for (UrlItem item : newData) {
        this.items.put(item.getName(), item);
      }
      notifyDataSetChanged();
    }

    public void add(UrlItem item) {
      items.put(item.getName(), item);
      notifyDataSetChanged();
    }
  }

  static class UrlItemHolder extends RecyclerView.ViewHolder {
    private final View view;
    private int position = -1;
    private UrlItem item;

    public UrlItemHolder(View v) {
      super(v);
      this.view = v;
    }

    public void setContent(UrlItem item, int position) {
      this.position = position;
      this.item = item;

      TextView nameField = view.findViewById(R.id.itemName);
      nameField.setText(item.getName());

      TextView urlField = view.findViewById(R.id.itemUrl);
      urlField.setText(item.getUrl());
    }
  }
}
