package com.algolia.insights;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ListItemAdapter extends ListAdapter<ListItem, ListItemViewHolder> {

    private List<ListItem> items;

    ListItemAdapter(@NonNull DiffUtil.ItemCallback<ListItem> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    void setListItems(List<ListItem> items) {
        this.items = items;
        submitList(items);
    }
}
