package org.docspell.docspellshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.Option;
import org.docspell.docspellshare.data.UrlItem;
import org.docspell.docspellshare.util.DataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  private static final int ADD_RESULT = 1;
  private static final int EDIT_RESULT = 2;

  private SwipeRefreshLayout swipeView;
  private RecyclerView urlListView;
  private DataStore dataStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataStore = new DataStore(this);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    urlListView = findViewById(R.id.urlListView);
    urlListView.setLayoutManager(new LinearLayoutManager(this));
    urlListView.setAdapter(new UrlItemAdapter(dataStore.loadAll(), dataStore.getDefault(), this));

    swipeView = findViewById(R.id.swipeCnt);
    swipeView.setOnRefreshListener(
        () -> {
          UrlItemAdapter ua = (UrlItemAdapter) urlListView.getAdapter();
          if (ua != null) {
            ua.replaceAll(dataStore.loadAll(), dataStore.getDefault());
          }
          swipeView.setRefreshing(false);
        });
  }

  public void addNewUrl(View view) {
    Intent intent = new Intent(this, AddUrlActivity.class);
    startActivityForResult(intent, ADD_RESULT);
  }

  public void editUrl(UrlItem item) {
    if (item != null) {
      Intent intent = new Intent(this, AddUrlActivity.class);
      intent.putExtra(AddUrlActivity.URL_ITEM_EXTRA, item);
      startActivityForResult(intent, EDIT_RESULT);
    } else {
      Log.e("not-found", "No item found in holder");
    }
  }

  public void deleteUrl(UrlItemAdapter adapter, UrlItem item) {
    if (item != null) {
      dataStore.remove(item.getName());
      adapter.remove(item);
    }
  }

  public void toggleDefault(UrlItemAdapter adapter, UrlItem item) {
    if (item != null) {
      adapter.toggleDefaultItem(item);
      if (dataStore.isDefault(item.getName())) {
        dataStore.removeDefault();
      } else {
        dataStore.setDefault(item.getName());
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data != null
        && (requestCode == ADD_RESULT || requestCode == EDIT_RESULT)
        && resultCode == RESULT_OK) {
      UrlItem item = (UrlItem) data.getSerializableExtra(UrlItem.class.getName());
      Log.i("add url", "item is " + item);
      if (item != null) {
        dataStore.addOrReplace(item);
        UrlItemAdapter ua = (UrlItemAdapter) urlListView.getAdapter();
        if (ua != null) {
          ua.add(item);
          if (ua.items.size() == 1) {
            dataStore.setDefault(item.getName());
            ua.toggleDefaultItem(item);
          }
        }
      }
    }
  }

  static class UrlItemAdapter extends RecyclerView.Adapter<UrlItemHolder> {
    private final Map<String, UrlItem> items;
    private Option<String> defaultItem = Option.empty();
    private final MainActivity activity;

    public UrlItemAdapter(List<UrlItem> items, Option<String> defaultItem, MainActivity activity) {
      this.activity = activity;
      this.items = new HashMap<>();
      this.defaultItem = defaultItem;
      for (UrlItem item : items) {
        this.items.put(item.getName(), item);
      }
    }

    private void toggleCards(View v) {
      View mainCard = v.findViewById(R.id.itemCard);
      View actionCard = v.findViewById(R.id.cardActions);
      boolean mainVisible = mainCard.getVisibility() == View.VISIBLE;
      if (mainVisible) {
        mainCard.setVisibility(View.GONE);
        actionCard.setVisibility(View.VISIBLE);
      } else {
        mainCard.setVisibility(View.VISIBLE);
        actionCard.setVisibility(View.GONE);
      }
    }

    @NonNull
    @Override
    public UrlItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.url_item_layout, parent, false);
      UrlItemHolder holder = new UrlItemHolder(v, activity, this);

      View backBtn = v.findViewById(R.id.itemCardCancelBtn);
      backBtn.setOnClickListener(view -> toggleCards(v));

      View mainCard = v.findViewById(R.id.itemCard);
      mainCard.setOnClickListener(view -> toggleCards(v));

      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UrlItemHolder holder, int position) {
      ArrayList<String> names = new ArrayList<>(items.keySet());
      Collections.sort(names);
      String name = names.get(position);
      UrlItem item = items.get(name);
      if (item != null) {
        holder.setContent(item, defaultItem, items.size());
      }
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    public void replaceAll(List<UrlItem> newData, Option<String> defaultItem) {
      items.clear();
      this.defaultItem = defaultItem;
      for (UrlItem item : newData) {
        this.items.put(item.getName(), item);
      }
      notifyDataSetChanged();
    }

    public void add(UrlItem item) {
      items.put(item.getName(), item);
      if (items.size() == 2) {
        notifyItemRangeChanged(0, items.size());
      } else {
        notifyDataSetChanged();
      }
    }

    public void remove(UrlItem item) {
      items.remove(item.getName());
      notifyDataSetChanged();
    }

    public void toggleDefaultItem(UrlItem item) {
      Log.w("debug", "Current default is: " + defaultItem + "; want it to be: " + item.getName());
      Option<String> di = Option.of(item.getName());
      if (defaultItem.equals(di)) {
        this.defaultItem = Option.empty();
      } else {
        this.defaultItem = di;
      }
      Log.w("debug", "Now default is: " + defaultItem);
      notifyItemRangeChanged(0, items.size());
    }
  }

  static class UrlItemHolder extends RecyclerView.ViewHolder {
    private final View view;
    private final MainActivity activity;
    private final UrlItemAdapter adapter;

    public UrlItemHolder(View v, MainActivity activity, UrlItemAdapter adapter) {
      super(v);
      this.view = v;
      this.activity = activity;
      this.adapter = adapter;
    }

    public void setContent(UrlItem item, Option<String> defaultItem, int size) {
      View actionCard = view.findViewById(R.id.cardActions);
      actionCard.setVisibility(View.GONE);

      CardView mainCard = view.findViewById(R.id.itemCard);
      mainCard.setVisibility(View.VISIBLE);

      TextView nameField = view.findViewById(R.id.itemName1);
      nameField.setText(item.getName());
      nameField = view.findViewById(R.id.itemName2);
      nameField.setText(item.getName());

      TextView urlField = view.findViewById(R.id.itemUrl);
      urlField.setText(item.getUrl());

      Switch defaultSwitch = view.findViewById(R.id.itemCardDefaultSwitch);
      boolean isDefault = defaultItem.equals(Option.of(item.getName()));
      defaultSwitch.setChecked(isDefault);
      int newColor =
          isDefault
              ? mainCard.getResources().getColor(R.color.isDefault)
              : mainCard.getResources().getColor(R.color.dsWhite);
      mainCard.setCardBackgroundColor(newColor);

      View deleteBtn = view.findViewById(R.id.itemCardDeleteBtn);
      deleteBtn.setOnClickListener(view -> activity.deleteUrl(adapter, item));
      View editBtn = view.findViewById(R.id.itemCardEditBtn);
      editBtn.setOnClickListener(view -> activity.editUrl(item));
      defaultSwitch.setOnClickListener(view -> activity.toggleDefault(adapter, item));
    }
  }
}
