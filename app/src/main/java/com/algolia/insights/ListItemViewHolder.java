package com.algolia.insights;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.instantsearch.insights.InstantSearchInsights;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


class ListItemViewHolder extends RecyclerView.ViewHolder {

    TextView textView = itemView.findViewById(R.id.textView);
    ImageView imageView = itemView.findViewById(R.id.imageView);
    Button button = itemView.findViewById(R.id.button);

    ListItemViewHolder(View itemView) {
        super(itemView);
    }

    void bind(final ListItem item) {
        textView.setText(item.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("queryID", item.getQueryId());
                map.put("objectID", item.getObjectId());
                InstantSearchInsights.shared(App.indexName).conversion(map);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("queryID", item.getQueryId());
                map.put("objectID", item.getObjectId());
                map.put("position", item.getPosition());
                InstantSearchInsights.shared(App.indexName).click(map);
            }
        });


        Glide
            .with(itemView.getContext())
            .load(item.getImage())
            .transition(withCrossFade())
            .into(imageView);

    }
}
