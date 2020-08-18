package org.docspell.docspellshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.UrlItem;
import org.docspell.docspellshare.util.DataStore;

public class ChooseUrlActivity extends AppCompatActivity {

  private SwipeRefreshLayout swipeView;
  private RecyclerView urlListView;
  private DataStore dataStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_url);
    this.dataStore = new DataStore(this);

    urlListView = findViewById(R.id.chooseUrlListView);
    urlListView.setLayoutManager(new LinearLayoutManager(this));
    urlListView.setAdapter(new UrlItemAdapter(dataStore.loadAll(), this));

    swipeView = findViewById(R.id.chooseSwipeCnt);
    swipeView.setOnRefreshListener(
        () -> {
          UrlItemAdapter ua = (UrlItemAdapter) urlListView.getAdapter();
          if (ua != null) {
            ua.replaceAll(dataStore.loadAll());
          }
          swipeView.setRefreshing(false);
        });
  }

  public void selectUrl(UrlItem urlItem) {
    Intent intent = new Intent();
    intent.putExtra(UrlItem.class.getName(), urlItem);
    setResult(RESULT_OK, intent);
    finish();
  }

  static class UrlItemAdapter extends RecyclerView.Adapter<UrlItemHolder> {
    private final List<UrlItem> items;
    private final ChooseUrlActivity activity;

    public UrlItemAdapter(List<UrlItem> items, ChooseUrlActivity activity) {
      this.activity = activity;
      this.items = items;
    }

    @NonNull
    @Override
    public UrlItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.url_item_layout, parent, false);
      UrlItemHolder holder = new UrlItemHolder(v, activity, this);
      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UrlItemHolder holder, int position) {
      UrlItem item = items.get(position);
      if (item != null) {
        holder.setContent(item);
      }
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    public void replaceAll(List<UrlItem> newData) {
      items.clear();
      items.addAll(newData);
      notifyDataSetChanged();
    }
  }

  static class UrlItemHolder extends RecyclerView.ViewHolder {
    private final View view;
    private final ChooseUrlActivity activity;
    private final UrlItemAdapter adapter;

    public UrlItemHolder(View v, ChooseUrlActivity activity, UrlItemAdapter adapter) {
      super(v);
      this.view = v;
      this.activity = activity;
      this.adapter = adapter;
    }

    public void setContent(UrlItem item) {
      View actionCard = view.findViewById(R.id.cardActions);
      actionCard.setVisibility(View.GONE);

      CardView mainCard = view.findViewById(R.id.itemCard);
      mainCard.setVisibility(View.VISIBLE);
      mainCard.setOnClickListener(view -> activity.selectUrl(item));

      TextView nameField = view.findViewById(R.id.itemName1);
      nameField.setText(item.getName());
      nameField = view.findViewById(R.id.itemName2);
      nameField.setText(item.getName());

      TextView urlField = view.findViewById(R.id.itemUrl);
      urlField.setText(item.getUrl());
    }
  }
}
