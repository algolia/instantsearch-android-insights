package com.algolia.insights;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.algolia.instantsearch.insights.Insights;
import com.algolia.instantsearch.insights.event.EventObjects;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


class ListItemViewHolder extends RecyclerView.ViewHolder {

    private TextView textView = itemView.findViewById(R.id.textView);
    private ImageView imageView = itemView.findViewById(R.id.imageView);
    private Button button = itemView.findViewById(R.id.button);

    ListItemViewHolder(View itemView) {
        super(itemView);
    }

    void bind(final ListItem item) {
        textView.setText(item.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Insights.shared(App.indexName).convertedAfterSearch(
                        "conversion",
                        item.getQueryId(),
                        new EventObjects.IDs(item.getObjectId()),
                        System.currentTimeMillis()
                );
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> positions = new ArrayList<>(1);

                positions.add(item.getPosition());
                Insights.shared(App.indexName).clickedAfterSearch(
                        "click",
                        item.getQueryId(),
                        new EventObjects.IDs(item.getObjectId()),
                        positions,
                        System.currentTimeMillis()
                );
            }
        });

        Glide
                .with(itemView.getContext())
                .load(item.getImage())
                .transition(withCrossFade())
                .into(imageView);

    }
}
