package com.appster.turtle.ui.signUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.ui.profile.EditProfileActivity;

import java.util.ArrayList;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 23/01/18.
 * Fragment for profile visibility
 */

public class ProfileVisibilitySelectorFragment extends SelectorFragment {

    private static final String SELECTED_DATA = "SELECTED_DATA";

    public static ProfileVisibilitySelectorFragment newInstance(ArrayList arrayList) {
        ProfileVisibilitySelectorFragment fragment = new ProfileVisibilitySelectorFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList(SELECTED_DATA, arrayList);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMaxSelection(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        hideDoneButton();
        hideSearch();
    }

    @Override
    String getTitle() {
        return getString(R.string.profile_visibility);
    }

    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    String getHint() {
        return "";
    }


    @Override
    void loadData(BaseSearchAdapter adapter, String query) {

        if (getArguments().getParcelableArrayList(SELECTED_DATA) != null && !getArguments().getParcelableArrayList(SELECTED_DATA).isEmpty())
            adapter.setSelectedData(getArguments().getParcelableArrayList(SELECTED_DATA));

        ArrayList<String> visibility = new ArrayList<>();
        visibility.add("Global");
        visibility.add("Campus");

        adapter.addAll(true, visibility);

    }

    @Override
    void dataSelected(ArrayList data) {

        if (getActivity() instanceof ProfileSetup2Activity)
            ((ProfileSetup2Activity) getActivity()).setProfileVisibility(data.get(0).toString());
        else
            ((EditProfileActivity) getActivity()).setProfileVisibility(data.get(0).toString());

    }

    @Override
    void scrollListener(BaseSearchAdapter adapter, LinearLayoutManager layoutManager, String query) {

    }
}
