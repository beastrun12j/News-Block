package com.example.newsblock;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>>
{
    private static final String LOG_TAG = NewsLoader.class.getName();

    private String mUrl;

    public NewsLoader(Context context, String url)
    {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading()
    {
        Log.e(LOG_TAG,"Starting the loader");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground()
    {
        Log.e(LOG_TAG,"Loading in Background");
        if (mUrl == null)
        {
            return null;
        }

        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }

}

