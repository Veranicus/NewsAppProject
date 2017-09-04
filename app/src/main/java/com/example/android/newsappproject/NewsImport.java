package com.example.android.newsappproject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;
/**
 * Created by Polacek on 16.7.2017.
 */

public class NewsImport extends AsyncTaskLoader<List<ListOfNews>> {

    private String mURL;

    public NewsImport(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<ListOfNews> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        // Performing network request and parching data
        List<ListOfNews> newsArticles = QueryUtils.fetchData(mURL);
        return newsArticles;
    }
}