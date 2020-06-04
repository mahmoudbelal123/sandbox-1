package com.appster.turtle.ui.signUp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.BaseSearchAdapter;
import com.appster.turtle.databinding.FragmentSelectorBinding;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

public abstract class SelectorFragment<T> extends BaseFragment implements BaseSearchAdapter.ItemSelected {

    private LinearLayoutManager layoutManager;
    private FragmentSelectorBinding databinding;
    private int maxSelection = 1;

    public BaseSearchAdapter<Object> getAdapter() {
        return adapter;
    }

    private BaseSearchAdapter<Object> adapter;

    public void setMaxSelection(int maxSelection) {

        this.maxSelection = maxSelection;
    }


    public SelectorFragment() {
    }

    @Override
    public void itemSelected(boolean isDone) {


        databinding.tvDone.setBackgroundResource(R.drawable.rounded_tv_bg);
        databinding.tvDone.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_white));

    }

    protected void hideSearch() {
        databinding.etSearch.setEnabled(false);
        databinding.etSearch.setInputType(InputType.TYPE_NULL);
        databinding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        databinding.vMajor.setVisibility(View.INVISIBLE);

    }

    @Override
    public void itemSelectionDone(ArrayList items) {

        dataSelected(items);

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
            getActivity().getSupportFragmentManager().popBackStack();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selector, container, false);

        initUI();

        return databinding.getRoot();
    }


    private void initUI() {
        databinding.etSearch.setHint(getHint());
        databinding.tvTitle.setText(getTitle());

        if (getHint().isEmpty()) {
            databinding.etSearch.setVisibility(View.GONE);
        }

        databinding.etSearch.requestFocus();


        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        databinding.rv.setLayoutManager(layoutManager);
        databinding.rv.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 23)));
        adapter = new BaseSearchAdapter<>(getActivity(), SelectorFragment.this, maxSelection);
        databinding.rv.setAdapter(adapter);

        loadData(adapter, "");

        databinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                loadData(adapter, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        databinding.rv.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard();
            return false;
        });


        databinding.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollListener(adapter, layoutManager, databinding.etSearch.getText().toString());


            }
        });

        databinding.rlTransparent.setOnClickListener(view -> {
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                getActivity().getSupportFragmentManager().popBackStack();

        });

        databinding.tvDone.setOnClickListener(view -> {


            dataSelected(adapter.getSelectedData());
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                getActivity().getSupportFragmentManager().popBackStack();

        });
    }

    public void hideDoneButton() {
        databinding.tvDone.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    abstract String getHint();

    abstract String getTitle();

    abstract void loadData(BaseSearchAdapter adapter, String query);

    abstract void dataSelected(ArrayList data);

    abstract void scrollListener(BaseSearchAdapter adapter, LinearLayoutManager layoutManager, String query);


}
