   /*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<EarthQuake>>{


    private static final int EARTH_QUAKE_LOADER_ID=1;
    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=3&limit=100";
    private quakeAdapter mEarthQuakeAdapter;
    private  TextView mEmptyTextview;
    private ProgressBar Loading_spinner;
    private ListView earthquakeListView;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ConnectivityManager connManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTH_QUAKE_LOADER_ID, null, this);

        }

        else{
            // first hide the loading spinner and displays the message no internet connection.
             View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyTextview=(TextView)findViewById(R.id.empty_view);
            mEmptyTextview.setText(R.string.No_Internet_Connection);


        }
            // get a refernce to the loaderManager in oder to intercat with the loaders.

            // Find a reference to the {@link ListView} in the layout
              earthquakeListView = (ListView) findViewById(R.id.list1);

            // Create a new {@link ArrayAdapter} of earthquakes
            mEarthQuakeAdapter = new quakeAdapter(this, R.layout.list_item, new ArrayList<EarthQuake>());

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(mEarthQuakeAdapter);

            mEmptyTextview = (TextView) findViewById(R.id.empty_view);
            earthquakeListView.setEmptyView(mEmptyTextview);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {   
                    EarthQuake quakeUrl=mEarthQuakeAdapter.getItem(position);
                    Uri EarthquakeUri=Uri.parse(quakeUrl.getUrl());
                    Intent WebsiteIntent=new Intent(Intent.ACTION_VIEW, EarthquakeUri);

                    // send the the intent to start a new activity by opening the url.
                    startActivity(WebsiteIntent);

                }
            });

        }

    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude=sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));
        Uri baseUri=Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder=baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit","10");
        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        return new Earthquakeloader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakes) {

        Loading_spinner=(ProgressBar)findViewById(R.id.loading_spinner);
        Loading_spinner.setVisibility(View.GONE);
        mEmptyTextview.setText(R.string.No_EarthQuake);
        // clear the adapter of the previous earthquake data.
        mEarthQuakeAdapter.clear();
        // if there is a valid list of earthquake data then add that to adpter.
        if(earthQuakes!=null&& !earthQuakes.isEmpty()){
            mEarthQuakeAdapter.addAll(earthQuakes);
        }


    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuake>> loader) {
        // on loader reset so that we can clear our existing data.
        mEarthQuakeAdapter.clear();;

    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     *
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */


}
