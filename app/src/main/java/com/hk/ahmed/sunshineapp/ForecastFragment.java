package com.hk.ahmed.sunshineapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public static ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {
    }

    private String defaultLocation;
    public String getLocation()
    {
        String locKey = getString(R.string.pref_location_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultLocation = sharedPreferences.getString(locKey,getString(R.string.pref_default_country_zip));
        return defaultLocation;
    }

    public String getTempUnit()
    {
        String tempKey = getString(R.string.pref_temperature_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String unit = sharedPreferences.getString(tempKey,getString(R.string.pref_default_temp_unit));
        return unit;
    }

    public void refreshData ()
    {
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        String location = getLocation();
        String unit = getTempUnit();

        fetchWeatherTask.execute(location,unit);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        refreshData();


    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String newLocation = sharedPreferences.getString("location",null);
        if(!(newLocation!= null && newLocation.equals(defaultLocation))){
            defaultLocation = newLocation;
            Toast.makeText(getContext(), "Refreshing..", Toast.LENGTH_SHORT).show();
            refreshData();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_location) {
            String location = getLocation();
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse("geo:0,0?q="+location));

            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_settings)
        {
            Intent intent = new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
                mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        ListView x = (ListView) rootView.findViewById(R.id.listView_forecast);
        x.setAdapter(mForecastAdapter);
        x.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra("Day",text);
                startActivity(intent);
            }
        }   );
        return rootView;
    }
}
