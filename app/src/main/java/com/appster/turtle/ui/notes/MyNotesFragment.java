/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MyNotesAdapter;
import com.appster.turtle.adapter.viewholder.MyNotesViewHolder;
import com.appster.turtle.adapter.viewholder.NotesViewHolder;
import com.appster.turtle.databinding.FragmentMyNotesTabbedBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.DeleteNotesRequest;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.NotesResponse;
import com.appster.turtle.network.response.RemoveMyNotesResponse;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;
/*
* Fragment for  note
 */
public class MyNotesFragment extends BaseFragment implements NotesViewHolder.INotesClicked, MyNotesViewHolder.INotesClicked {

    private FragmentMyNotesTabbedBinding mBinding;
    private RetrofitRestRepository mRepository;
    private LinearLayoutManager linearLayoutManager;
    private List<NotesModel> mNotesList;

    private int mCurrentPage = 1;

    private int mTotalPagesAvailable;
    private BaseCallback baseCallback;
    private MyNotesAdapter adapter;
    private boolean isPurchased;

    @Override
    public String getFragmentName() {
        return MyNotesFragment.class.getName();
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    public MyNotesFragment() {
        //
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyNotesFragment newInstance(int sectionNumber) {
        MyNotesFragment fragment = new MyNotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_notes_tabbed, container, false);

        isPurchased = getArguments().getInt(ARG_SECTION_NUMBER) == 0;

        initUI();


        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotesFromApi("");

    }

    private void initUI() {


        mBinding.includedClSearch.etSearch.setHint("Search notes");


        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mNotesList = new ArrayList<>();


        mBinding.includedClSearch.etSearch.addTextChangedListener(searchTextWatcher);

        mBinding.rvPurchasedNotes.addOnScrollListener(purchaseScrollListener);
        mBinding.rvPurchasedNotes.setLayoutManager(linearLayoutManager);
      //  mBinding.rvPurchasedNotes.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 6.2f)));
        mBinding.rvPurchasedNotes.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 15f)));

    }


    private boolean isLoading;
    private RecyclerView.OnScrollListener purchaseScrollListener = new RecyclerView.OnScrollListener() {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int mVisibleItemCount = linearLayoutManager.getChildCount();
            int mTotalItemCount = linearLayoutManager.getItemCount();
            int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                if (mNotesList != null) {
                    mCurrentPage++;
                    isLoading = true;
                    getNotesFromApi("");
                }
            }
        }
    };


    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mBinding.includedClSearch.etSearch.getText().toString().trim().length() > 1) {

                getNotesFromApi(charSequence.toString());
            }

            if (mBinding.includedClSearch.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchAllNotes()) {
                    getNotesFromApi("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    public void getNotesFromQuery(String query) {
        mCurrentPage = 1;
        getNotesFromApi(query);

    }


    private void getNotesFromApi(String query) {

        if (query.isEmpty()) {
            showProgressBar();
            PreferenceUtil.setLastSearchWasAllNotes(true);
        } else {
            PreferenceUtil.setLastSearchWasAllNotes(false);
        }

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<NotesResponse>(getBaseActivity(), mRepository.getApiService().searchSelfNotes(mCurrentPage, Constants.LIST_LENGTH, query, isPurchased ? 0 : 1)) {
            @Override
            public void onSuccess(NotesResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {
                    mNotesList = response.getData();
                    adapter = new MyNotesAdapter(getActivity(), mNotesList, MyNotesFragment.this);
                    mBinding.rvPurchasedNotes.setAdapter(adapter);
                } else {
                    mNotesList.addAll((response.getData()));
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFail() {
                isLoading = false;

            }
        };

    }

    @Override
    public void onNoteClicked(int noteId, NotesModel model, final int pos) {

        showProgressBar();

        new BaseCallback<NoteResponse>(getBaseActivity(), mRepository.getApiService().noteDetails(noteId), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.NOTES, response.getData());
                bundle.putBoolean(Constants.PurchaseNotes.FROM_MY_NOTES, true);
                bundle.putInt(Constants.POS, pos);
                ExplicitIntent.getsInstance().navigateTo(getActivity(), NotesPurchaseActivity.class, bundle);
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    @Override
    public void onMenuClicked(final NotesModel notesModel, final int pos) {
        ((MyNotesActivity) getActivity()).onMenuClicked(notesModel, pos);

    }


    @Override
    public void onDeleteClicked(final int noteId, final int pos) {
        Alert.createYesNoAlert(getActivity(), getString(R.string.delete), getString(R.string.delete_notes_message), new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                deleteNote(noteId, pos);
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();

            }
        });
    }

    private void deleteNote(int noteId, final int pos) {
        DeleteNotesRequest deleteNotesRequest = new DeleteNotesRequest(noteId);

        new BaseCallback<RemoveMyNotesResponse>(getBaseActivity(), mRepository.getApiService().removeMyNotes(deleteNotesRequest), true) {

            @Override
            public void onSuccess(RemoveMyNotesResponse response) {

                if (response.getData().isSuccess()) {
                    mNotesList.remove(pos);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }

    public void onRemoveItemFromAdapter(int pos) {
        adapter.removeNote(pos);
    }

}
