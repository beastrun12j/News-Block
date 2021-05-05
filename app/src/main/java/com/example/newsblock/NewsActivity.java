package com.example.newsblock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private NewsAdapter mAdapter;

    public static final String LOG_TAG = NewsActivity.class.getName();

    private static final String GUARDIAN_NEWS_URL = "http://content.guardianapis.com/search";

    private static final int NEWS_LOADER_ID = 1;

    private TextView mEmptyStateTextView;

    private ProgressBar mLoadingIndicator;

    public String searchQuery = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(LOG_TAG, "OnCreate Method is called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        mAdapter = new NewsAdapter(this, new ArrayList<>());
        ListView newsListView = findViewById(R.id.news_list_item);
        newsListView.setAdapter(mAdapter);

        mLoadingIndicator = findViewById(R.id.loadingIndicator);
        mLoadingIndicator.setIndeterminate(true);

        SearchView newsSearchView = findViewById(R.id.search_view);

        mEmptyStateTextView = findViewById(R.id.emptyView);
        newsListView.setEmptyView(mEmptyStateTextView);

        newsListView.setOnItemClickListener((parent, view, position, l) -> {
            News currentNews = mAdapter.getItem(position);

            Uri newsUri = Uri.parse(currentNews.getUrl());

            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

            startActivity(websiteIntent);

        });

        newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                newsSearchView.clearFocus();

                mEmptyStateTextView.setText("");

                mAdapter.clear();

                searchQuery = newsSearchView.getQuery().toString();

                Log.e(LOG_TAG, searchQuery);

                mLoadingIndicator.setVisibility(View.VISIBLE);
                getLoaderManager().restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            Log.e(LOG_TAG, "Loader Manager Called");
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loadingIndicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText("No Internet connection.");
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_order_by_menu_key),
                getString(R.string.settings_order_by_menu_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_date_menu_key),
                getString(R.string.settings_order_date_menu_default)
        );

        Uri baseUri = Uri.parse(GUARDIAN_NEWS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", minMagnitude);
        uriBuilder.appendQueryParameter("order-date", orderBy);
        uriBuilder.appendQueryParameter("api-key","test");

        Log.e(LOG_TAG, uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        mLoadingIndicator.setVisibility(View.GONE);

        Log.e(LOG_TAG, "Loader Task finished");

        mEmptyStateTextView.setText("No News Found.");

        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.e(LOG_TAG, "Resetting the Loader");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}