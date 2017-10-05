package com.alperencan.news.android.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alperencan.news.android.R;
import com.alperencan.news.android.model.Content;

import java.util.List;

/**
 * {@link ContentAdapter} creates ContentViewHolder classes as needed and binds them to their data.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<Content> newsItems;

    public ContentAdapter(List<Content> newsItems) {
        this.newsItems = newsItems;
    }

    /**
     * Called when RecyclerView needs a new {@link ContentViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ContentViewHolder, int)
     */
    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ContentViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ContentViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        Content newsItem = newsItems.get(position);

        holder.titleView.setText(newsItem.getTitle());
        holder.sectionView.setText(newsItem.getSection());
        holder.authorView.setText(newsItem.getAuthor());
        holder.dateView.setText(newsItem.getDate());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView titleView;
        TextView sectionView;
        TextView authorView;
        TextView dateView;

        ContentViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;

            titleView = cardView.findViewById(R.id.title);
            sectionView = cardView.findViewById(R.id.section);
            authorView = cardView.findViewById(R.id.author);
            dateView = cardView.findViewById(R.id.date);

        }
    }
}
