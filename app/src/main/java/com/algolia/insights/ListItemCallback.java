package com.algolia.insights;

import androidx.recyclerview.widget.DiffUtil;


class ListItemCallback extends DiffUtil.ItemCallback<ListItem> {

    @Override
    public boolean areItemsTheSame(ListItem oldItem, ListItem newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(ListItem oldItem, ListItem newItem) {
        return true;
    }
}
