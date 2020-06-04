package com.appster.turtle.ui.signUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetGraduationResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.EditProfileActivity;

import java.util.ArrayList;

import rx.Observable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 08/12/17.
 * Activity class for year search
 */

public class YearSearchFragment extends SelectorFragment {

    private static final String SELECTED_DATA = "selected_data";
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private int mTotalPagesAvailable;
    private boolean isLoading;


    public YearSearchFragment() {
    }


    @Override
    String getTitle() {
        return getString(R.string.graduation);
    }


    public static YearSearchFragment newInstance(ArrayList arrayList) {
        YearSearchFragment fragment = new YearSearchFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList(SELECTED_DATA, arrayList);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    String getHint() {
        return "";
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        hideDoneButton();
        hideSearch();

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

        if (getActivity() instanceof ProfileSetup1Activity)
            ((ProfileSetup1Activity) getActivity()).setYear(data);
        else
            ((EditProfileActivity) getActivity()).setYear(data);

    }

    private void getData(final BaseSearchAdapter adapter, String query) {
        final RetrofitRestRepository repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();

        Observable<GetGraduationResponse> observable = repository.getApiService().getGraduationYear(mCurrentPage, Constants.LIST_LENGTH);
        new BaseCallback<GetGraduationResponse>((BaseActivity) getActivity(), observable, true) {

            @Override
            public void onSuccess(GetGraduationResponse response) {

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
