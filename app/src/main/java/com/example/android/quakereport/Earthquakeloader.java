package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class Earthquakeloader extends AsyncTaskLoader<List<EarthQuake>> {
    // LOG_TAG for log messsages.
    private static final String LOG_TAG=Earthquakeloader.class.getName();

    // private string url
    private String  mUrl;
    public Earthquakeloader(Context context,String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /* This is on a background Thread */
    @Override
    public List<EarthQuake> loadInBackground() {
        if(mUrl==null)
            return null;
        List<EarthQuake> earthQuakes=QueryUtils.fetchEarthquakeData(mUrl);
        return earthQuakes;

    }

}
