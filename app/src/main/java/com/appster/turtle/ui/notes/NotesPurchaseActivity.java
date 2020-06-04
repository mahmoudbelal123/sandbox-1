/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.AttachmentsAdapter;
import com.appster.turtle.databinding.ActivityPurchaseLayoutBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.databinding.LayoutPurchasedNotesBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.DeleteNotesRequest;
import com.appster.turtle.network.request.PaymentRequest;
import com.appster.turtle.network.response.BaseMessageResponse;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.PaymentResponse;
import com.appster.turtle.network.response.RemoveMyNotesResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.payment.CardListActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

import java.util.List;
/*
* Activity for purchase note
 */
public class NotesPurchaseActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPurchaseLayoutBinding mBinding;
    private NotesModel mNotes;

    private AttachmentsAdapter mAttachmentsAdapter;
    private RetrofitRestRepository mRepository;
    private String imagePath = "";
    private LayoutLeaveRoomBinding deleteBottomSheetBinding;
    private LayoutPurchasedNotesBinding reviewBottomSheetBinding;
    private boolean update;
    private boolean isNewAdded = false;
    private int pos = 0;
    private List<Attachment> mAttachmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_layout);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        setUpHeader(mBinding.header.clHeader, getString(R.string.notes), "", R.drawable.back_arrow);


        if (getIntent().hasExtra(Constants.IS_NEWADD)) {
            isNewAdded = true;
            getnote(getIntent().getExtras().getInt(Constants.BundleKey.NOTE_ID));

        } else {
            isNewAdded = false;
            mNotes = (NotesModel) getIntent().getExtras().get(Constants.NOTES);
            pos = getIntent().getIntExtra(Constants.POS, 0);
            initUI();
        }
        registerReceiver(notesUpdate, new IntentFilter("notes.review"));


    }

    private void initUI() {

        deleteBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_leave_room, mBinding.bsReview, false);
        deleteBottomSheetBinding.tvLeave.setText(R.string.delete);
        deleteBottomSheetBinding.tvLeave.setOnClickListener(this);
        deleteBottomSheetBinding.tvCancel.setOnClickListener(this);
        deleteBottomSheetBinding.tvEditNotes.setOnClickListener(this);
        deleteBottomSheetBinding.tvEmailNotes.setOnClickListener(this);

        toggleViewsVisibility();

        mBinding.setNotes(mNotes);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setAutoMeasureEnabled(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvAttachments.setLayoutManager(manager);
        mBinding.rvAttachments.setNestedScrollingEnabled(false);

        mBinding.rvAttachments.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 7.7f)));
        mAttachmentsList = mNotes.getAttachments();
        mAttachmentsAdapter = new AttachmentsAdapter(this, mAttachmentsList);
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);

        setPostedByText();
        mBinding.tvPostNotes.setOnClickListener(this);
        mBinding.header.ivIconStart.setOnClickListener(this);

        reviewBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_purchased_notes, mBinding.bsReview, false);
        reviewBottomSheetBinding.tvSkip.setOnClickListener(this);
        reviewBottomSheetBinding.tvWriteReview.setOnClickListener(this);


        mBinding.ivMenu.setOnClickListener(this);
        mBinding.tvReviewCount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.NOTE_ID, mNotes.getId());
            ExplicitIntent.getsInstance().navigateTo(NotesPurchaseActivity.this, ReviewListActivity.class, bundle);
        });
        mBinding.srbReviews.setRating(mNotes.getAvgRating());

    }

    private void setPostedByText() {
        String fullString = getString(R.string.posted_by_user) + mNotes.getPostedBy();
        SpannableString ss = new SpannableString(fullString);
        Typeface usernameFont = Typeface.createFromAsset(getAssets(), getString(R.string.eina_01_semibold));
        int startIndex = fullString.indexOf(mNotes.getPostedBy()) - 1;
        int endIndex = startIndex + mNotes.getPostedBy().length() + 1;
        ss.setSpan(new CustomTypefaceSpan("", usernameFont), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Bundle i = new Bundle();
                i.putInt(Constants.USER_ID, mNotes.getUsersListModel().getUserId());
                ExplicitIntent.getsInstance().navigateTo(NotesPurchaseActivity.this, UserProfileActivity.class, i);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                // link color
                ds.setColor(Color.parseColor("#ffad82"));
                ds.setUnderlineText(false);
            }
        }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvPostedBy.setText(ss);
        mBinding.tvPostedBy.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvPostedBy.setHighlightColor(Color.TRANSPARENT);

    }

    private void toggleViewsVisibility() {
        if (getIntent().getExtras().getBoolean(Constants.PurchaseNotes.FROM_POST_NOTES)) {
            fromPostNotes();
        } else if (getIntent().getExtras().getBoolean(Constants.PurchaseNotes.FROM_NOTES_LISTING) ||
                getIntent().getExtras().getBoolean(Constants.PurchaseNotes.FROM_MY_NOTES)) {

            if (mNotes.getPostedByMe()) {
                fromPostNotes();
            } else {

                if (mNotes.getPurchasedByMe()) {

                    setUpHeader(mBinding.header.clHeader, getString(R.string.notes), "", R.drawable.back_arrow);
                    mBinding.ivMenu.setVisibility(View.VISIBLE);

                    deleteBottomSheetBinding.tvEmailNotes.setVisibility(View.VISIBLE);
                    deleteBottomSheetBinding.tvLeave.setVisibility(View.GONE);
                    deleteBottomSheetBinding.tvEditNotes.setVisibility(View.GONE);


                    if (!mNotes.getReviewedByMe())
                        mBinding.tvPostNotes.setText(R.string.write_a_review);
                    else
                        mBinding.tvPostNotes.setVisibility(View.GONE);

                } else {

                    setUpHeader(true, mBinding.header.clHeader, getString(R.string.notes), "");
                    mBinding.ivMenu.setVisibility(View.INVISIBLE);
                    mBinding.tvPostNotes.setText(R.string.purchase_notes_str);
                    mBinding.tvPostedBy.setVisibility(View.VISIBLE);
                    mBinding.tvNotes.setVisibility(View.GONE);
                }


            }
        } else {
            fromPostNotes();
            //From My Notes is same is From Post Notes
        }
    }

    private void fromPostNotes() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.notes), "", R.drawable.back_arrow);
        mBinding.ivMenu.setVisibility(View.VISIBLE);
        mBinding.tvPostNotes.setVisibility(View.GONE);
        mBinding.tvPostedBy.setVisibility(View.GONE);
        deleteBottomSheetBinding.tvEmailNotes.setVisibility(View.GONE);
        deleteBottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);
        deleteBottomSheetBinding.tvEditNotes.setVisibility(View.VISIBLE);


    }

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.tv_post_notes:
                if (!mNotes.getPurchasedByMe()) {

                    bundle.putBoolean(Constants.BundleKey.PAYMENT_CARD, true);
                    ExplicitIntent.getsInstance().navigateForResult(this, CardListActivity.class, Constants.REQUEST_CODE.REQUEST_NOTES_PAYMENT, bundle);


                } else {
                    writeReview();
                }
                break;

            case R.id.iv_icon_start:
                onBackPressed();
                break;

            case R.id.iv_menu:

                mBinding.bsReview.showWithSheetView(deleteBottomSheetBinding.getRoot());

                break;

            case R.id.tv_leave:

                mBinding.bsReview.dismissSheet();
                deleteNotAction();
                deleteNote();

                break;
            case R.id.tv_cancel:

                mBinding.bsReview.dismissSheet();

                break;

            case R.id.tv_write_review:

                mBinding.bsReview.dismissSheet();
                writeReview();

                break;

            case R.id.tv_skip:

                ExplicitIntent.getsInstance().navigateTo(this, NotesListingActivity.class);

                mBinding.bsReview.dismissSheet();

                break;


            case R.id.tv_edit_notes:
                editNotes();
                mBinding.bsReview.dismissSheet();
                break;

            case R.id.tv_email_notes:

                mBinding.bsReview.dismissSheet();
                sendEmail();
                break;

            default:
                break;
        }
    }

    private void editNotes() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.NOTES, mNotes);
        bundle.putBoolean(Constants.IS_EDIT, true);
        ExplicitIntent.getsInstance().navigateTo(this, CreateNotesActivity.class, bundle);
        mBinding.bsReview.dismissSheet();
    }

    private void writeReview() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.NOTES, mNotes);
        if (!isNewAdded) {
            bundle.putInt(Constants.POS, pos);
        }
        ExplicitIntent.getsInstance().navigateTo(this, ReviewNotesActivity.class, bundle);

    }

    @Override
    public void onBackPressed() {

        if (getIntent().getExtras().getBoolean(Constants.PurchaseNotes.FROM_MY_NOTES)) {

            finish();
        }
        if (getIntent().hasExtra(Constants.IS_NEWADD)) {
            finish();
        } else
            ExplicitIntent.getsInstance().navigateTo(NotesPurchaseActivity.this, NotesListingActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private void deleteNotAction() {
        Alert.createYesNoAlert(NotesPurchaseActivity.this, getString(R.string.delete), getString(R.string.delete_notes_message), new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                deleteNote();
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();

            }
        }).show();
    }

    public void deleteNote() {
        DeleteNotesRequest deleteNotesRequest = new DeleteNotesRequest(mNotes.getId());

        new BaseCallback<RemoveMyNotesResponse>(NotesPurchaseActivity.this, mRepository.getApiService().removeMyNotes(deleteNotesRequest), true) {

            @Override
            public void onSuccess(RemoveMyNotesResponse response) {

                if (response.getData().isSuccess()) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.PurchaseNotes.FROM_POST_NOTES, true);
                    bundle.putBoolean(Constants.IS_DELETED, true);
                    bundle.putInt(Constants.POS, pos);
                    ExplicitIntent.getsInstance().navigateTo(NotesPurchaseActivity.this, NotesListingActivity.class, bundle);
                    finish();
                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }


    public void purchaseNote(String customerId, String cardId) {
        PaymentRequest paymentRequest = new PaymentRequest(cardId, customerId);

        new BaseCallback<PaymentResponse>(NotesPurchaseActivity.this, mRepository.getApiService().paymentByCard(mNotes.getId(), paymentRequest), true) {

            @Override
            public void onSuccess(PaymentResponse response) {

                if (response.getData().isStatus()) {

                    mBinding.tvPostNotes.setText(R.string.write_a_review);
                    mNotes.setPurchasedByMe(true);

                    mBinding.bsReview.showWithSheetView(reviewBottomSheetBinding.getRoot());

                }

            }

            @Override
            public void onFail() {

                showSnackBar(getString(R.string.common_err_msg));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (update) {
            new BaseCallback<NoteResponse>(NotesPurchaseActivity.this, mRepository.getApiService().noteDetails(mNotes.getId()), true) {

                @Override
                public void onSuccess(NoteResponse response) {
                    if (response != null) {
                        mNotes = response.getData();

                        //update title, class, description, review count, snippet
                        mBinding.setNotes(mNotes);

                        //update attachments
                        mAttachmentsList.clear();
                        mAttachmentsList.addAll(mNotes.getAttachments());
                        mAttachmentsAdapter.notifyDataSetChanged();

                        //snippet
                        CameraBindingAdapters.bindImageUrl(mBinding.ivSnippet, mNotes.getSnippetUrl());

                        if (mNotes.getPurchasedByMe()) {
                            if (!mNotes.getReviewedByMe())
                                mBinding.tvPostNotes.setText(R.string.write_a_review);
                            else
                                mBinding.tvPostNotes.setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onFail() {
//
                }
            };
        }
    }

    BroadcastReceiver notesUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            update = true;

        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(notesUpdate);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE.REQUEST_NOTES_PAYMENT:

                    if (data != null && data.getExtras() != null) {

                        String customerId = data.getExtras().getString(Constants.BundleKey.PAYMENT_CUSTOMER_ID);
                        String cardId = data.getExtras().getString(Constants.BundleKey.PAYMENT_CARD_ID);
                        purchaseNote(customerId, cardId);


                    }
                    break;

                case Constants.REQUEST_CODE.NOTES_UPDATED:

                    break;

                default:
                    break;


            }
        }
    }

    public void sendEmail() {

        new BaseCallback<BaseMessageResponse>(NotesPurchaseActivity.this, mRepository.getApiService().resendEmail(mNotes.getId()), true) {

            @Override
            public void onSuccess(BaseMessageResponse response) {

                if (response.getData().isSuccess()) {

                    showSnackBar(getString(R.string.email_sent_msg));

                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }

    private void getnote(int noteId) {
        new BaseCallback<NoteResponse>(NotesPurchaseActivity.this, mRepository.getApiService().noteDetails(noteId), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                mNotes = response.getData();
                initUI();
            }

            @Override
            public void onFail() {
//
            }
        };
    }
}
