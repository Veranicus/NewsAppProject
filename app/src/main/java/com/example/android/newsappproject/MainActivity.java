package com.example.android.newsappproject;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ListOfNews>>, SwipeRefreshLayout.OnRefreshListener {
    /**
     * Guardian's api url
     */
    private static final String REQUEST_URL = "https://content.guardianapis.com/search?page-size=20&show-tags=contributor&show-fields=all&api-key=test";
    private LoaderManager loaderManager;
    private ListView listView;
    private ListOfNewsAdapter adapter;
    private TextView emptyStateTextView;
    private ProgressBar mLoader;
    private Button searchButton = null;
    private EditText searchField = null;
    private String query = null;
    private SwipeRefreshLayout listRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //refresh listener for refreshing by swiping
        listRefresh.setOnRefreshListener(this);
        searchButton = (Button) findViewById(R.id.search_news_button);
        //finding the id of edit text search field
        searchField = (EditText) findViewById(R.id.searchEdit);
        //Finding the id of a list view
        listView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        // Reference from loader manager to interact with loaders
        mLoader = (ProgressBar) findViewById(R.id.loading_indicator);
        loaderManager = getLoaderManager();
        //checking for internet connection
        if (isConnected()) {
            loaderManager.initLoader(1, null, this);
        } else {
            //setting visibility to gone
            mLoader.setVisibility(View.GONE);
            //in a case there is no internet connection, display appropriate message
            String message = getString(R.string.noInternetConnection);
            new AlertDialog.Builder(this).setMessage(message).show();
        }
        // setting an empty view
        listView.setEmptyView(emptyStateTextView);
        //For showing the items in List of News
        adapter = new ListOfNewsAdapter(this, new ArrayList<ListOfNews>());
        listView.setAdapter(adapter);
        // Click listener's logic
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListOfNews itemsList = (ListOfNews) adapterView.getAdapter().getItem(position);
                Uri webpage;
                String title = itemsList.getWebTitle();
                if (itemsList.getWebUrl() != null) {
                    webpage = Uri.parse(itemsList.getWebUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "\"" + title + "\" " + getString(R.string.not_available), Toast.LENGTH_LONG).show();
                }
            }
        });
        // After clicking buttons on keyboard
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        // After clicking search button
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search() {
        //Checking connection after search button is clicked on
        if (isConnected()) {
            query = searchField.getText().toString();
            mLoader.setVisibility(View.VISIBLE);
            emptyStateTextView.setText("");
            //restart the loader with the new data
            loaderManager.restartLoader(1, null, this);
        } else {
            String message = getString(R.string.noInternetConnection);
            new AlertDialog.Builder(this).setMessage(message).show();
        }
    }

    @Override
    public Loader<List<ListOfNews>> onCreateLoader(int i, Bundle bundle) {
        //geting preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Stored preferences
        String maxResults = sharedPrefs.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default)
        );
        //URL of the Guardian
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (query != null && !query.isEmpty()) {
            uriBuilder.appendQueryParameter("q", query);
            //adding query to a search box
        }
        uriBuilder.appendQueryParameter("page-size", maxResults); //maxResults listed
        uriBuilder.appendQueryParameter("order-by", orderBy.toLowerCase()); //defining the order
        if (!section.equals("all")) {
            uriBuilder.appendQueryParameter("section", section.toLowerCase()); //filtring the results
        }
        return new NewsImport(this, uriBuilder.toString());
    }

    @Override //after finished loading
    public void onLoadFinished(Loader<List<ListOfNews>> loader, List<ListOfNews> data) {
        if (isConnected()) {
            // Clearing adapter
            adapter.clear();
            //Hiding the loading bar
            mLoader.setVisibility(View.GONE);
            String message = getString(R.string.no_book, query);
            emptyStateTextView.setText(message);
            // Adding valid list of news to adapter
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            }
        } else {
            mLoader.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.noInternetConnection);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ListOfNews>> loader) {
        // Reseting loader
        adapter.clear();
    }

    //Opening search preferences menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, activitySettings.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Determining active connection
    private Boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onRefresh() {
        if (isConnected()) {
            loaderManager.restartLoader(1, null, this);
        } else {
            String message = getString(R.string.noInternetConnection);
            new AlertDialog.Builder(this).setMessage(message).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listRefresh.setRefreshing(false);
            }
        }, 3000);
    }


}
