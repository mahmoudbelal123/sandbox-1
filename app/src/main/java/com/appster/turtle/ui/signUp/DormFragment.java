package com.appster.turtle.ui.signUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetDormResponse;
import com.appster.turtle.network.response.GetGreekLifeResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.EditProfileActivity;

import java.util.ArrayList;

import rx.Observable;
/*
* Fragment for Dorm
 */
public class DormFragment extends SelectorFragment {

    private static final String IS_DORM = "is_dorm";
    private static final String SELECTED_DATA = "selected_data";
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private int mTotalPagesAvailable;
    private boolean isLoading;


    public DormFragment() {
    }


    public static DormFragment newInstance(boolean isDorm, ArrayList arrayList) {
        DormFragment fragment = new DormFragment();
        Bundle b = new Bundle();
        b.putBoolean(IS_DORM, isDorm);
        b.putParcelableArrayList(SELECTED_DATA, arrayList);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getArguments().getBoolean(IS_DORM))
            setMaxSelection(2);
    }

    @Override
    String getTitle() {
        return "Dorm";
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getArguments().getBoolean(IS_DORM))
            hideDoneButton();
//        else
//            hideSearch();
    }

    @Override
    public String getFragmentName() {
        return DormFragment.class.getName();
    }

    @Override
    String getHint() {
        return getArguments().getBoolean(IS_DORM) ? getString(R.string.search_dorm) : getString(R.string.select_greek_life);
    }

    @Override
    void loadData(BaseSearchAdapter adapter, String query) {
        mCurrentPage = 1;
        if (getArguments().getParcelableArrayList(SELECTED_DATA) != null && !getArguments().getParcelableArrayList(SELECTED_DATA).isEmpty())
            adapter.setSelectedData(getArguments().getParcelableArrayList(SELECTED_DATA));

        getData(adapter, query);

    }

    @Override
    void dataSelected(ArrayList data) {

        if (getActivity() instanceof ProfileSetup2Activity)
            ((ProfileSetup2Activity) getActivity()).setDorm(data);
        else
            ((EditProfileActivity) getActivity()).setDorm(data);


    }


    private void getData(final BaseSearchAdapter adapter, String query) {
        if (!getArguments().getBoolean(IS_DORM) && !query.isEmpty()) {
            ///Greek Life local search
//            adapter.searchLocal(query);

            return;
        }

        final RetrofitRestRepository repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();

        Observable observable = repository.getApiService().getGreekLife();
        if (getArguments().getBoolean(IS_DORM))
            observable = repository.getApiService().getDorm(mCurrentPage, Constants.LIST_LENGTH, query);

        new BaseCallback<BaseResponse>((BaseActivity) getActivity(), observable, query.isEmpty()) {

            @Override
            public void onSuccess(BaseResponse response) {

                if (!getArguments().getBoolean(IS_DORM)) {
                    if (response != null) {
                        mTotalPagesAvailable = ((GetGreekLifeResponse) response).getPagination().getTotalPages();
                        isLoading = false;

                        if (mCurrentPage == 1)
                            adapter.addAll(true, ((GetGreekLifeResponse) response).getData());
                        else
                            adapter.addAll(((GetGreekLifeResponse) response).getData());

                    }
                } else {
                    if (response != null) {
                        mTotalPagesAvailable = ((GetDormResponse) response).getPagination().getTotalPages();
                        isLoading = false;

                        if (mCurrentPage == 1)
                            adapter.addAll(true, ((GetDormResponse) response).getData());
                        else
                            adapter.addAll(((GetDormResponse) response).getData());

                    }
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
//            if (data != null) {
            mCurrentPage++;
            isLoading = true;
            getData(adapter, query);

//            }
        }
    }

}
