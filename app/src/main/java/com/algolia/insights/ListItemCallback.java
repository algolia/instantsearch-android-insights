package com.algolia.insights;

import android.support.v7.util.DiffUtil;


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
