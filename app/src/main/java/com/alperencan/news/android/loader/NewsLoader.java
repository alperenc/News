package com.alperencan.news.android.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.alperencan.news.android.model.Content;
import com.alperencan.news.android.utils.QueryUtils;

import java.util.List;

/**
 * Loads a list of news items by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<Content>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Content> loadInBackground() {
        if (this.url == null) {
            return null;
        }

        // Perform the network request, parse the response, extract a list of news items and return.
        return QueryUtils.fetchNewsData(this.url);
    }
}