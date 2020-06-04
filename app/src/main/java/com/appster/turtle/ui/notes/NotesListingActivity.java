package com.appster.turtle.ui.notes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.NotesAdapter;
import com.appster.turtle.adapter.viewholder.NotesViewHolder;
import com.appster.turtle.databinding.ActivityNotesListingNewBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.DeleteNotesRequest;
import com.appster.turtle.network.response.GetBankDetailResponse;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.NotesResponse;
import com.appster.turtle.network.response.RemoveMyNotesResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.payment.BankAccountActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.CustomEditText;

import java.util.ArrayList;
import java.util.List;
/*
* Activity for listing note
 */
public class NotesListingActivity extends BaseActivity implements View.OnClickListener, NotesViewHolder.INotesClicked {

    private ActivityNotesListingNewBinding mBinding;
    private RetrofitRestRepository mRepository;
    private NotesAdapter adapter;
    private List<NotesModel> mNotesList;
    private LinearLayoutManager linearLayoutManager;
    private BaseCallback baseCallback;

    private LayoutLeaveRoomBinding deleteBottomSheetBinding;
    int mCurrentPage = 1, mTotalPagesAvailable = 0;
    private String query = "";


    private boolean isLoading;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(NotesListingActivity.this, R.layout.activity_notes_listing_new);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);


        mBinding.toolbar.setTitleTextColor(ContextCompat.getColor(NotesListingActivity.this, R.color.black));

        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            LogUtils.LOGD("OFFSET", verticalOffset + "");
            LogUtils.LOGD("OFFSET TOTAL", Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() + "");
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                mBinding.toolbar.setBackgroundResource(R.color.app_white);
                mBinding.mainToolbarTitle.setText(getString(R.string.notes));
            } else {
                //Expanded


                if (Math.abs(verticalOffset) > 160) {
                    mBinding.toolbar.setBackgroundResource(R.color.app_white);
                    mBinding.mainToolbarTitle.setText(getString(R.string.notes));
                } else {
                    mBinding.toolbar.setBackgroundResource(R.color.transparent);
                    mBinding.mainToolbarTitle.setText("");
                }
            }

        });

        initUI();
        setupEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu:
                ExplicitIntent.getsInstance().navigateTo(NotesListingActivity.this, MyNotesActivity.class);

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra(Constants.PurchaseNotes.FROM_POST_NOTES)) {
            boolean isNewAdded = false;
            if (intent.hasExtra(Constants.IS_NEWADD)) {
                isNewAdded = true;
            }
            if (intent.getExtras().getBoolean(Constants.IS_EDIT)) {
                adapter.updateNote(intent.getIntExtra(Constants.POS, 0), intent.getParcelableExtra(Constants.NOTES));
            } else if (isNewAdded) {
                adapter.addNote(intent.getParcelableExtra(Constants.NOTES));
            } else if (intent.getExtras().getBoolean(Constants.IS_DELETED)) {
                adapter.removeNote(intent.getIntExtra(Constants.POS, 0));
            } else if (intent.getExtras().getBoolean(Constants.IS_BACK)) {
                showProgressBar();
                mCurrentPage = 1;
                getNotesFromApi("");
            } else {
                showProgressBar();
                getNotesFromApi(query);
            }
        }
    }

    private void initUI() {

        setUpHeader(mBinding.header.clHeader, "", R.drawable.back_arrow, R.drawable.ic_menu_icon_black);

        mBinding.includedClSearch.etSearch.setHint(R.string.search_notes_on_noise);


        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            LogUtils.LOGD("OFFSET", verticalOffset + "");
            LogUtils.LOGD("OFFSET TOTAL", Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() + "");
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                mBinding.toolbar.setBackgroundResource(R.color.app_white);
            } else {
                //Expanded


                if (Math.abs(verticalOffset) > 160) {
                    mBinding.toolbar.setBackgroundResource(R.color.app_white);
                } else {
                    mBinding.toolbar.setBackgroundResource(R.color.transparent);
                }
            }

        });


        //set click listeners
        mBinding.header.ivIconEnd.setOnClickListener(this);
        mBinding.fab.setOnClickListener(this);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(NotesListingActivity.this, LinearLayoutManager.VERTICAL, false);
        mNotesList = new ArrayList<>();

        mBinding.includedClSearch.etSearch.addTextChangedListener(searchTextWatcher);

        mBinding.rvNotes.addOnScrollListener(scrollListener);
        mBinding.rvNotes.setLayoutManager(linearLayoutManager);
        mBinding.rvNotes.addItemDecoration(new ItemDecorationView(NotesListingActivity.this, 0, Utils.dpToPx(NotesListingActivity.this, 15f)));
        getNotesFromApi(query);
        deleteBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_leave_room, mBinding.bsReview, false);
        deleteBottomSheetBinding.tvLeave.setText(R.string.delete_note);
        mBinding.rvNotes.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return false;
        });

        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);
            mCurrentPage = 1;
            getNotesFromApi("");
        });

    }


    private void editNotes(NotesModel notesModel, int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.NOTES, notesModel);
        bundle.putBoolean(Constants.IS_EDIT, true);
        bundle.putInt(Constants.POS, pos);
        ExplicitIntent.getsInstance().navigateTo(this, CreateNotesActivity.class, bundle);
        mBinding.bsReview.dismissSheet();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_end:
                ExplicitIntent.getsInstance().navigateTo(NotesListingActivity.this, MyNotesActivity.class);
                break;
            case R.id.fab:
                checkBankDetail();
                break;

            default:
                break;
        }
    }


    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int mVisibleItemCount = linearLayoutManager.getChildCount();
            int mTotalItemCount = linearLayoutManager.getItemCount();
            int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                if (mNotesList != null) {
                    mCurrentPage++;
                    setLoading(true);
                    isLoading = true;
                    query = "";
                    getNotesFromApi(query);
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

                //suggestive search
                mCurrentPage = 1;
                query = charSequence.toString();
                getNotesFromApi(query);
            } else {
            }

            if (mBinding.includedClSearch.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchAllNotes()) {
                    mCurrentPage = 1;
                    query = "";
                    getNotesFromApi(query);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };


    private void getNotesFromApi(String query) {

        if (query.isEmpty()) {
            if (mCurrentPage == 1)
                showProgressBar();

            PreferenceUtil.setLastSearchWasAllNotes(true);
        } else {
            PreferenceUtil.setLastSearchWasAllNotes(false);
        }

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<NotesResponse>(this, mRepository.getApiService().searchNotes(mCurrentPage, Constants.LIST_LENGTH, query)) {
            @Override
            public void onSuccess(NotesResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;
                mBinding.swipeRefresh.setRefreshing(false);
                if (mCurrentPage == 1) {
                    mNotesList = response.getData();
                    adapter = new NotesAdapter(NotesListingActivity.this, mNotesList, NotesListingActivity.this);
                    mBinding.rvNotes.setAdapter(adapter);
                    setLoading(false);
                } else {
                    setLoading(false);
                    mNotesList.addAll((response.getData()));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFail() {
                mBinding.swipeRefresh.setRefreshing(false);
            }
        };

    }

    public void setLoading(boolean isLoading) {
        adapter.setLoading(isLoading);
    }

    @Override
    public void onNoteClicked(int noteId, NotesModel notesModel, final int pos) {
        showProgressBar();
        if (baseCallback != null)
            baseCallback.cancel();
        baseCallback = new BaseCallback<NoteResponse>(this, mRepository.getApiService().noteDetails(noteId), true) {
            @Override
            public void onSuccess(NoteResponse response) {
                hideProgressBar();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.NOTES, response.getData());
                bundle.putInt(Constants.POS, pos);
                bundle.putBoolean(Constants.PurchaseNotes.FROM_NOTES_LISTING, true);
                ExplicitIntent.getsInstance().navigateTo(NotesListingActivity.this, NotesPurchaseActivity.class, bundle);
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };

    }

    @Override
    public void onMenuClicked(final NotesModel notesModel, final int pos) {
        deleteBottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);
        deleteBottomSheetBinding.tvEditNotes.setVisibility(View.VISIBLE);
        deleteBottomSheetBinding.tvEmailNotes.setVisibility(View.GONE);
        mBinding.bsReview.showWithSheetView(deleteBottomSheetBinding.getRoot());
        deleteBottomSheetBinding.tvLeave.setOnClickListener(v -> Alert.createYesNoAlert(NotesListingActivity.this, getString(R.string.delete), getString(R.string.delete_notes_message), new Alert.OnAlertClickListener() {
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
        deleteBottomSheetBinding.tvEditNotes.setOnClickListener(v -> {
            mBinding.bsReview.dismissSheet();
            editNotes(notesModel, pos);
        });
        deleteBottomSheetBinding.tvCancel.setOnClickListener(v -> mBinding.bsReview.dismissSheet());
    }


    private void checkBankDetail() {
        RetrofitRestRepository mRepository = ((ApplicationController) getApplication()).provideRepository();
        new BaseCallback<GetBankDetailResponse>(this, mRepository.getApiService().getBankDetail(), true, false) {

            @Override
            public void onSuccess(GetBankDetailResponse response) {
                hideProgressBar();
                if (response.getData() != null) {
                    ExplicitIntent.getsInstance().navigateTo(NotesListingActivity.this, CreateNotesActivity.class);
                } else
                    showBankDetailDialog();
            }

            @Override
            public void onFail() {

                hideProgressBar();
                showBankDetailDialog();

            }
        };
    }

    private void showBankDetailDialog() {
        Alert.createYesNoAlert(NotesListingActivity.this, "", getString(R.string.add_bank_details_alert), getString(R.string.ok), "Cancel", new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                dialog.dismiss();
                Bundle b = new Bundle();
                b.putBoolean(Constants.BundleKey.FROM_SETTINGS, false);
                ExplicitIntent.getsInstance().navigateTo(NotesListingActivity.this, BankAccountActivity.class, b);
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        }).show();

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
                    adapter.removeNote(pos);
                    mBinding.bsReview.dismissSheet();
                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }


    public void setupEditText() {
        // Any time edit text instances lose their focus, dismiss the keyboard!
        mBinding.includedClSearch.etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !(mBinding.includedClSearch.etSearch.findFocus() instanceof CustomEditText)) {
                hideKeyboard(v);
            } else {
                showKeyboard(v);
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }
}
