/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.MyNotesViewHolder;
import com.appster.turtle.adapter.viewholder.NotesViewHolder;
import com.appster.turtle.databinding.ActivityMyNotesBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.DeleteNotesRequest;
import com.appster.turtle.network.response.RemoveMyNotesResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
/*
* Activity for mynote
 */
public class MyNotesActivity extends BaseActivity implements View.OnClickListener, NotesViewHolder.INotesClicked, MyNotesViewHolder.INotesClicked {


    private RetrofitRestRepository mRepository;
    private ActivityMyNotesBinding mBinding;

    private LayoutLeaveRoomBinding deleteBottomSheetBinding;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_notes);

        initUI();
    }

    @Override
    public String getActivityName() {
        return MyNotesActivity.class.getName();
    }

    private void initUI() {

        setUpHeader(true, mBinding.header.clHeader, getString(R.string.my_notes_cap));

        mBinding.etSearch.setHint("Search notes");

        mBinding.header.ivIconStart.setOnClickListener(v -> onBackPressActivity());
        mBinding.tvPostedHeader.setOnClickListener(this);
        mBinding.tvPurchasedHeader.setOnClickListener(this);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        mBinding.etSearch.addTextChangedListener(searchTextWatcher);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.

        deleteBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_leave_room, mBinding.bsReview, false);
        deleteBottomSheetBinding.tvLeave.setText(R.string.delete_note);


        mBinding.container.setAdapter(mSectionsPagerAdapter);
        mBinding.container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBinding.etSearch.setText("");
                        mBinding.tvPostedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.gray));
                        mBinding.tvPurchasedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.black));
                        mBinding.vPostedIndicator.setVisibility(View.INVISIBLE);
                        mBinding.vPurchasedIndicator.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mBinding.etSearch.setText("");
                        mBinding.tvPostedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.black));
                        mBinding.tvPurchasedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.gray));
                        mBinding.vPostedIndicator.setVisibility(View.VISIBLE);
                        mBinding.vPurchasedIndicator.setVisibility(View.INVISIBLE);
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });


    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mBinding.etSearch.getText().toString().trim().length() > 1) {
                mSectionsPagerAdapter.getCurrentFragment().getNotesFromQuery(charSequence.toString());
            }
            if (mBinding.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchAllNotes()) {
                    mSectionsPagerAdapter.getCurrentFragment().getNotesFromQuery("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onNoteClicked(int noteId, NotesModel notesModel, int pos) {
//
    }

    @Override
    public void onDeleteClicked(int noteId, int position) {
//
    }

    @Override
    public void onMenuClicked(final NotesModel notesModel, final int pos) {
        deleteBottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);
        deleteBottomSheetBinding.tvEditNotes.setVisibility(View.VISIBLE);
        deleteBottomSheetBinding.tvEmailNotes.setVisibility(View.GONE);
        mBinding.bsReview.showWithSheetView(deleteBottomSheetBinding.getRoot());
        deleteBottomSheetBinding.tvLeave.setOnClickListener(v -> Alert.createYesNoAlert(MyNotesActivity.this, getString(R.string.delete), getString(R.string.delete_notes_message), new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                deleteNote(notesModel, pos);
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();

            }
        }).show());
        deleteBottomSheetBinding.tvCancel.setOnClickListener(v -> mBinding.bsReview.dismissSheet());

        deleteBottomSheetBinding.tvEditNotes.setOnClickListener(v -> {
            mBinding.bsReview.dismissSheet();
            editNotes(notesModel);
        });

    }

    private void editNotes(NotesModel notesModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.NOTES, notesModel);
        bundle.putBoolean(Constants.IS_EDIT, true);
        ExplicitIntent.getsInstance().navigateTo(this, CreateNotesActivity.class, bundle);
        mBinding.bsReview.dismissSheet();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        MyNotesFragment postFragment, purchaseFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        public MyNotesFragment getCurrentFragment() {
            int pos = mBinding.container.getCurrentItem();
            if (pos == 0) {
                if (purchaseFragment == null) {
                    return null;
                }
                return purchaseFragment;
            } else {
                if (postFragment == null) {
                    return null;
                }
                return postFragment;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (purchaseFragment == null) {
                    purchaseFragment = MyNotesFragment.newInstance(position);
                }
                return purchaseFragment;
            } else {
                if (postFragment == null) {
                    postFragment = MyNotesFragment.newInstance(position);
                }
                return postFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    mBinding.tvPostedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.gray));
                    mBinding.tvPurchasedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.black));
                    return "PURCHASED";
                case 1:
                    mBinding.tvPostedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.black));
                    mBinding.tvPurchasedHeader.setTextColor(ContextCompat.getColor(MyNotesActivity.this, R.color.gray));
                    return "POSTED";

                default:
                    break;

            }
            return null;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_posted_header:

                mBinding.container.setCurrentItem(1);

                break;

            case R.id.tv_purchased_header:
                mBinding.container.setCurrentItem(0);

                break;
            default:
                break;
        }

    }

    public void deleteNote(NotesModel mNotes, final int pos) {
        DeleteNotesRequest deleteNotesRequest = new DeleteNotesRequest(mNotes.getId());

        new BaseCallback<RemoveMyNotesResponse>(this, mRepository.getApiService().removeMyNotes(deleteNotesRequest), true) {

            @Override
            public void onSuccess(RemoveMyNotesResponse response) {

                if (response.getData().isSuccess()) {

                    Intent intent = new Intent();
                    intent.setAction(getString(R.string.delete_notes_action));
                    sendBroadcast(intent);
                    mSectionsPagerAdapter.getCurrentFragment().onRemoveItemFromAdapter(pos);
                    mBinding.bsReview.dismissSheet();
                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressActivity();
    }

    private void onBackPressActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.PurchaseNotes.FROM_POST_NOTES, true);
        bundle.putBoolean(Constants.IS_BACK, true);
        ExplicitIntent.getsInstance().navigateTo(MyNotesActivity.this, NotesListingActivity.class, bundle);
        finish();
    }
}
