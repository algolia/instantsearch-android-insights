package com.algolia.insights;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class DemoActivity extends AppCompatActivity implements CompletionHandler, SearchView.OnQueryTextListener {

    ListItemAdapter adapter = new ListItemAdapter(new ListItemCallback());
    Query query = new Query();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        App app = (App) getApplication();
        SearchView searchView = findViewById(R.id.searchView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        query.setQuery("");
        query.setClickAnalytics(true);
        app.index.searchAsync(query, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        App app = (App) getApplication();

        query.setQuery(s);
        app.index.searchAsync(query, this);
        return false;
    }

    @Override
    public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
        try {
            JSONArray hits = jsonObject.getJSONArray("hits");
            ArrayList<ListItem> items = new ArrayList<>(hits.length());

            for (int index = 0; index < hits.length(); index++) {
                JSONObject object = hits.getJSONObject(index);
                ListItem item = new ListItem(
                    object.getString("name"),
                    object.getString("image"),
                    jsonObject.getString("queryID"),
                    object.getString("objectID"),
                    index + 1
                );
                items.add(item);
            }
            adapter.setListItems(items);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
