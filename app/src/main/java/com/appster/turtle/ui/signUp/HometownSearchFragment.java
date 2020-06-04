package com.appster.turtle.ui.signUp;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.ui.profile.EditProfileActivity;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PlaceAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 08/12/17
 * search for home town.
 */

public class HometownSearchFragment extends SelectorFragment implements GoogleApiClient.OnConnectionFailedListener {

    PlaceAPI mPlaceAPI = new PlaceAPI();

    private ArrayList<AutocompletePrediction> mPlacesPredictionsList = new ArrayList<>();
    private ArrayList<String> mList = new ArrayList<>();
    private PendingResult<AutocompletePredictionBuffer> results;

    @Override
    public String getFragmentName() {
        return HometownSearchFragment.class.getName();
    }

    @Override
    public void onStart() {
        super.onStart();
        hideDoneButton();
    }

    @Override
    String getTitle() {
        return "Home Town";
    }

    @Override
    String getHint() {
        return getString(R.string.search_hometown);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    void loadData(final BaseSearchAdapter adapter, final String query) {


        mPlacesPredictionsList.clear();
        adapter.addAll(true, mPlacesPredictionsList);
        adapter.notifyDataSetChanged();

        if (results != null)
            results.cancel();

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        GoogleApiClient googleApiClient;
        if (getActivity() instanceof ProfileSetup1Activity)
            googleApiClient = ((ProfileSetup1Activity) getActivity()).getmGoogleApiClient();
        else
            googleApiClient = ((EditProfileActivity) getActivity()).getmGoogleApiClient();


        results = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, query, null, filter);


        results.setResultCallback(autocompletePredictions -> {


            LogUtils.LOGD("Location", "Query" + query + " Size" + mPlacesPredictionsList.size());

            if (autocompletePredictions.getStatus().isSuccess()) {
                mList.clear();

                for (AutocompletePrediction prediction : autocompletePredictions) {
                    //Add as a new item to avoid IllegalArgumentsException when buffer is released
                    LogUtils.LOGD("kk", prediction.getFullText(null) + "  ");
                    String name = (String) prediction.getFullText(null);
                    String[] array = name.split(",");
                    String countryName;
                    if (name.endsWith("USA")) {
                        countryName = array[0].concat(" , ").concat(array[1]);
                    } else {
                        countryName = array[0].concat(" , ").concat(array[array.length - 1]);

                    }

                    mList.add(countryName);
                }
            }

            if (query.isEmpty()) {
                mList.clear();
                adapter.addAll(true, mList);
                adapter.notifyDataSetChanged();
            } else {
                adapter.addAll(true, mList);
                adapter.notifyDataSetChanged();
            }


        });

    }

    @Override
    void dataSelected(ArrayList data) {


        if (getActivity() instanceof ProfileSetup1Activity) {
            if (!data.isEmpty())
                ((ProfileSetup1Activity) getActivity()).setHomeTown((String) data.get(0));
            else
                ((ProfileSetup1Activity) getActivity()).setHomeTown(null);
        } else {
            if (!data.isEmpty())
                ((EditProfileActivity) getActivity()).setHomeTown((String) data.get(0));
            else
                ((EditProfileActivity) getActivity()).setHomeTown(null);

        }


    }

    @Override
    void scrollListener(BaseSearchAdapter adapter, LinearLayoutManager layoutManager, String query) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
