package com.appster.turtle.ui.signUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetMajorResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.EditProfileActivity;

import java.util.ArrayList;

import rx.Observable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 08/12/17.
 * Activity for major search
 */

public class MajorSearchFragment extends SelectorFragment {

    private static final String IS_MAJOR = "is_major";
    private static final String SELECTED_DATA = "selected_data";
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private int maxSelection = 2;


    public MajorSearchFragment() {
    }


    public static MajorSearchFragment newInstance(boolean isMajor, ArrayList arrayList) {
        MajorSearchFragment fragment = new MajorSearchFragment();
        Bundle b = new Bundle();
        b.putBoolean(IS_MAJOR, isMajor);
        b.putParcelableArrayList(SELECTED_DATA, arrayList);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    String getTitle() {
        return getString(getArguments().getBoolean(IS_MAJOR) ? R.string.major : R.string.minor);
    }

    @Override
    String getHint() {
        return getString(getArguments().getBoolean(IS_MAJOR) ? R.string.search_major : R.string.search_minor);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMaxSelection(2);
    }

    @Override
    void loadData(final BaseSearchAdapter adapter, String query) {

        mCurrentPage = 1;
        if (getArguments().getParcelableArrayList(SELECTED_DATA) != null && !getArguments().getParcelableArrayList(SELECTED_DATA).isEmpty())
            adapter.setSelectedData(getArguments().getParcelableArrayList(SELECTED_DATA));

        getData(adapter, query);
    }

    @Override
    void dataSelected(ArrayList data) {

        if (getActivity() instanceof EditProfileActivity) {

            if (getArguments().getBoolean(IS_MAJOR))
                ((EditProfileActivity) getActivity()).setMajor(data);
            else
                ((EditProfileActivity) getActivity()).setMinor(data);

        } else {
            if (getArguments().getBoolean(IS_MAJOR))
                ((ProfileSetup1Activity) getActivity()).setMajor(data);
            else
                ((ProfileSetup1Activity) getActivity()).setMinor(data);
        }


    }

    private void getData(final BaseSearchAdapter adapter, String query) {
        final RetrofitRestRepository repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();

        Observable<GetMajorResponse> observable = repository.getApiService().getMajor(mCurrentPage, Constants.LIST_LENGTH, query);
        if (!getArguments().getBoolean(IS_MAJOR))
            observable = repository.getApiService().getMinor(mCurrentPage, Constants.LIST_LENGTH, query);

        new BaseCallback<GetMajorResponse>((BaseActivity) getActivity(), observable, query.isEmpty()) {

            @Override
            public void onSuccess(GetMajorResponse response) {

                if (response != null) {
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;

                    if (mCurrentPage == 1)
                        adapter.addAll(true, response.getData());
                    else
                        adapter.addAll(response.getData());

                }


            }

            @Override
            public void onFail() {

            }
        };

    }

    @Override
    void scrollListener(BaseSearchAdapter adapter, LinearLayoutManager layoutManager, String query) {
        mVisibleItemCount = layoutManager.getChildCount();
        mTotalItemCount = layoutManager.getItemCount();
        mPastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
        if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
            mCurrentPage++;
            isLoading = true;
            getData(adapter, query);


        }
    }


    @Override
    public String getFragmentName() {
        return MajorSearchFragment.class.getName();
    }
}
