package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.databinding.FragmentGreekLifeSelectorBinding;
import com.appster.turtle.model.GreekLife;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetGreekLifeResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.EditProfileActivity;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 23/01/18.
 * Activity geek life selector
 */

public class GreekLifeSelectorFragment extends BaseFragment implements View.OnClickListener {


    private static final String SELECTED_FRAT = "selected_frat";
    private static final String SELECTED_SOR = "selected_sor";
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private FragmentGreekLifeSelectorBinding databinding;
    private BaseSearchAdapter<GreekLife> adapterSor;
    private BaseSearchAdapter<GreekLife> adapter;
    private ArrayList<GreekLife> fraternities = new ArrayList<>();
    private ArrayList<GreekLife> sororities = new ArrayList<>();


    public GreekLifeSelectorFragment() {
    }


    public static GreekLifeSelectorFragment newInstance(ArrayList fratArrayList, ArrayList sorArrayList) {
        GreekLifeSelectorFragment fragment = new GreekLifeSelectorFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList(SELECTED_FRAT, fratArrayList);
        b.putParcelableArrayList(SELECTED_SOR, sorArrayList);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!getArguments().getBoolean(IS_DORM))
//            setMaxSelection(2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_greek_life_selector, container, false);

        initUI();


        return databinding.getRoot();
    }

    private void initUI() {

        databinding.tvFrat.setOnClickListener(this);
        databinding.tvSor.setOnClickListener(this);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        databinding.rvFrat.setLayoutManager(layoutManager);
        databinding.rvFrat.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 23)));
        adapter = new BaseSearchAdapter<>(getActivity(), fratSelected, 2);
        databinding.rvFrat.setAdapter(adapter);

        getData();

        databinding.rvFrat.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });

        databinding.rvSor.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });


        databinding.etFratSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getActivity().getCurrentFocus() == databinding.etFratSearch) {
                    // is only executed if the EditText was directly changed by the user
                    databinding.etSorSearch.setText(charSequence.toString());
                    searchLocal(charSequence.toString());
                }
//                getData(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        databinding.rvFrat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollListener(adapter, layoutManager, databinding.etFratSearch.getText().toString());


            }
        });

        final LinearLayoutManager layoutManagerSor = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        databinding.rvSor.setLayoutManager(layoutManagerSor);
        databinding.rvSor.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 23)));
        adapterSor = new BaseSearchAdapter<>(getActivity(), sorSelected, 2);
        databinding.rvSor.setAdapter(adapterSor);


        databinding.etSorSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (getActivity().getCurrentFocus() == databinding.etSorSearch) {
                    // is only executed if the EditText was directly changed by the user
                    databinding.etFratSearch.setText(charSequence.toString());
                    searchLocal(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        databinding.rvSor.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollListener(adapterSor, layoutManagerSor, databinding.etSorSearch.getText().toString());


            }
        });


        databinding.rlTransparent.setOnClickListener(view -> {
//                getActivity().getSupportFragmentManager().beginTransaction().remove(SelectorFragment.this).commit();
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                getActivity().getSupportFragmentManager().popBackStack();

        });

        databinding.tvDone.setOnClickListener(view -> {
//                getActivity().getSupportFragmentManager().beginTransaction().remove(SelectorFragment.this).commit();


            if (getActivity() instanceof ProfileSetup2Activity)
                ((ProfileSetup2Activity) getActivity()).setGreekLife(adapter.getSelectedData(), adapterSor.getSelectedData());
            else
                ((EditProfileActivity) getActivity()).setGreekLife(adapter.getSelectedData(), adapterSor.getSelectedData());


            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                getActivity().getSupportFragmentManager().popBackStack();

        });


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_frat:

                databinding.tvSor.setBackgroundResource(R.drawable.bg_tab_right_unselected);
                databinding.tvFrat.setBackgroundDrawable(null);
                databinding.rlFrat.setVisibility(View.VISIBLE);
                databinding.rlSor.setVisibility(View.INVISIBLE);

                break;

            case R.id.tv_sor:

                databinding.tvFrat.setBackgroundResource(R.drawable.bg_tab_left_unselected);
                databinding.tvSor.setBackgroundDrawable(null);
                databinding.rlSor.setVisibility(View.VISIBLE);
                databinding.rlFrat.setVisibility(View.INVISIBLE);


                break;


        }

    }

    private void searchLocal(String query) {
        if (!query.isEmpty()) {
            ///Greek Life local search
            searchLocal(true, query);
            searchLocal(false, query);

            return;
        } else {
//            mCurrentPage=1;
//            getData();

            adapter.addAll(true, fraternities);
            adapterSor.addAll(true, sororities);
        }

    }


    public void searchLocal(boolean isFrat, String query) {
//        if(query.length()<3)
//            return;

        ArrayList<GreekLife> arrayList = new ArrayList<>();
        for (GreekLife greekLife : isFrat ? fraternities : sororities) {

            if (greekLife.getValue().toLowerCase().contains(query.toLowerCase())) {
                arrayList.add(greekLife);
            }
        }

//        addAll(true, (ArrayList<T> )arrayList);
//
//
//        notifyDataSetChanged();

        if (isFrat)
            adapter.addAll(true, arrayList);
        else
            adapterSor.addAll(true, arrayList);


    }

    private void getData() {

        final RetrofitRestRepository repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();

        Observable observable = repository.getApiService().getGreekLife();


        new BaseCallback<GetGreekLifeResponse>((BaseActivity) getActivity(), observable, true) {

            @Override
            public void onSuccess(GetGreekLifeResponse response) {


                if (response != null) {
                    if (response != null) {
//                        mTotalPagesAvailable = ((GetGreekLifeResponse) response).getPagination().getTotalPages();
//                        isLoading = false;

//                            if (mCurrentPage == 1)
//                                adapter.addAll(true, ((GetGreekLifeResponse) response).getData());
//                            else
//                                adapter.addAll(((GetGreekLifeResponse) response).getData());

                        filter(response.getData());

                    }

                }


            }

            @Override
            public void onFail() {

            }
        };

    }


    private void scrollListener(BaseSearchAdapter adapter, LinearLayoutManager layoutManager, String query) {
//        mVisibleItemCount = layoutManager.getChildCount();
//        mTotalItemCount = layoutManager.getItemCount();
//        mPastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
//        if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
////            if (data != null) {
//            mCurrentPage++;
//            isLoading = true;
//            getData();
//
////            }
//        }
    }


    private void toggleDone() {
        if (adapterSor.getSelectedData().isEmpty() && adapter.getSelectedData().isEmpty()) {

            databinding.tvDone.setBackgroundResource(R.drawable.btn_unselected_bg);
            databinding.tvDone.setTextColor(ContextCompat.getColor(getActivity(), R.color.done_unselected_color));

        } else {

            databinding.tvDone.setBackgroundResource(R.drawable.rounded_tv_bg);
            databinding.tvDone.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_white));


        }

    }


    private boolean isFratDone, isSorDone;
    BaseSearchAdapter.ItemSelected fratSelected = new BaseSearchAdapter.ItemSelected() {
        @Override
        public void itemSelected(boolean isDone) {
            isFratDone = isDone;
            toggleDone();
        }

        @Override
        public void itemSelectionDone(ArrayList item) {

        }
    };

    BaseSearchAdapter.ItemSelected sorSelected = new BaseSearchAdapter.ItemSelected() {
        @Override
        public void itemSelected(boolean isDone) {
            isSorDone = isDone;
            toggleDone();
        }

        @Override
        public void itemSelectionDone(ArrayList item) {

        }
    };


    private void filter(List<GreekLife> greekLives) {
        fraternities = new ArrayList<>();
        sororities = new ArrayList<>();


        for (GreekLife greekLife : greekLives) {


            if (greekLife.getType().equalsIgnoreCase(Constants.FRATERNITIES))
                fraternities.add(greekLife);
            else
                sororities.add(greekLife);


        }

        if (getArguments().getParcelableArrayList(SELECTED_FRAT) != null && !getArguments().getParcelableArrayList(SELECTED_FRAT).isEmpty())
            adapter.setSelectedData(getArguments().getParcelableArrayList(SELECTED_FRAT));

        if (getArguments().getParcelableArrayList(SELECTED_SOR) != null && !getArguments().getParcelableArrayList(SELECTED_SOR).isEmpty())
            adapterSor.setSelectedData(getArguments().getParcelableArrayList(SELECTED_SOR));


        if (mCurrentPage == 1) {
            adapter.addAll(true, fraternities);
            adapterSor.addAll(true, sororities);
        } else {

            adapter.addAll(fraternities);
            adapterSor.addAll(sororities);
        }


    }


}
