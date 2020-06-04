/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.payment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.CardAdapter;
import com.appster.turtle.adapter.viewholder.CardViewHolder;
import com.appster.turtle.databinding.ActivityCardListBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.model.CardModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.DeleteCardResponse;
import com.appster.turtle.network.response.GetCardsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
/*
* Activity for card list
 */
public class CardListActivity extends BaseActivity implements View.OnClickListener {
    private static final int ADD_CARD_REQUEST = 1233;
    private ActivityCardListBinding mBinding;
    private RetrofitRestRepository mRepository;
    private ArrayList<CardModel> cardModels;
    private CardAdapter cardAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private LayoutLeaveRoomBinding bottomSheetBinding;
    private CardModel cardToDelete;
    private boolean isPurchasing;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_list);

        mRepository = ((ApplicationController) getApplication()).provideRepository();
        initUI();


    }


    @Override
    protected void onResume() {
        super.onResume();
        getCardList();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, "Payment", R.drawable.ic_close_black_small, 0);


        cardModels = new ArrayList<>();

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Constants.BundleKey.PAYMENT_CARD, false))
            isPurchasing = true;

        cardAdapter = new CardAdapter(CardListActivity.this, cardModels, new CardViewHolder.ICardClicked() {
            @Override
            public void onCardDeleteClicked(CardModel pos) {

                cardToDelete = pos;
                mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());

            }

            @Override
            public void onCardClicked(CardModel card) {

                if (isPurchasing) {
                    Bundle b = new Bundle();
                    b.putString(Constants.BundleKey.PAYMENT_CARD_ID, card.getCardId());
                    b.putString(Constants.BundleKey.PAYMENT_CUSTOMER_ID, customerId);
                    Intent i = new Intent();
                    i.putExtras(b);
                    setResult(RESULT_OK, i);
                    finish();
                }

            }
        });

        mBinding.rvCards.setAdapter(cardAdapter);

        linearLayoutManager = new LinearLayoutManager(CardListActivity.this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = linearLayoutManager.getChildCount();
                int mTotalItemCount = linearLayoutManager.getItemCount();
                int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (cardModels != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getCardList();
                    }
                }
            }
        });
        mBinding.rvCards.setLayoutManager(linearLayoutManager);
        mBinding.rvCards.addItemDecoration(new ItemDecorationView(CardListActivity.this, 0, Utils.dpToPx(CardListActivity.this, 14)));


        mBinding.tvAddCard.setOnClickListener(this);


        addBottomsheet();
    }


    private void addBottomsheet() {
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(CardListActivity.this), R.layout.layout_leave_room, mBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvLeave.setText(R.string.delete);
        bottomSheetBinding.tvCancel.setText(R.string.cancel);

        bottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);


        bottomSheetBinding.tvLeave.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);
    }

    private void addNewCard(Intent intent) {

        if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.CARD) && intent.getExtras().getParcelable(Constants.BundleKey.CARD) != null) {

            CardModel cardModel = intent.getExtras().getParcelable(Constants.BundleKey.CARD);
            cardModels.add(0, cardModel);
            if (cardModels.size() == 1)
                cardAdapter.notifyDataSetChanged();
            else {
                cardAdapter.notifyItemInserted(0);
                mBinding.rvCards.smoothScrollToPosition(0);
            }

        }
    }

    private void getCardList() {


        new BaseCallback<GetCardsResponse>(this, mRepository.getApiService().getCards(mCurrentPage, Constants.LIST_LENGTH), mCurrentPage == 1) {

            @Override
            public void onSuccess(GetCardsResponse response) {
                hideProgressBar();

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {
                    cardModels.clear();

                }

                if (response.getData().get(0).getData() != null && !response.getData().get(0).getData().isEmpty()) {

                    customerId = response.getData().get(0).getCustomerId();
                    cardModels.addAll(response.getData().get(0).getData());
                    cardAdapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onFail() {

                hideProgressBar();
            }
        };
    }

    @Override
    public String getActivityName() {
        return CardListActivity.class.getName();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_add_card:
                if (cardModels.size() < 3) {
                    ExplicitIntent.getsInstance().navigateForResult(CardListActivity.this, AddCardActivity.class, ADD_CARD_REQUEST);

                } else {

                    showSnackBar(getString(R.string.cant_add_more_card));
                }

                break;

            case R.id.iv_icon_start:

                finish();
                break;

            case R.id.tv_leave:

                mBinding.bottomSheetMembers.dismissSheet();
                deleteCard(cardModels.indexOf(cardToDelete));

                break;

            case R.id.tv_cancel:
                mBinding.bottomSheetMembers.dismissSheet();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_CARD_REQUEST:
                if (resultCode == RESULT_OK) {
                    addNewCard(data);
                }

                break;
            default:
                break;
        }

    }

    private void deleteCard(final int pos) {


        new BaseCallback<DeleteCardResponse>(this, mRepository.getApiService().deleteCards(cardModels.get(pos).getCardId()), true) {

            @Override
            public void onSuccess(DeleteCardResponse response) {
                hideProgressBar();


                if (response.getData().isStatus()) {

                    cardModels.remove(pos);
                    cardAdapter.notifyItemRemoved(pos);
                }


            }

            @Override
            public void onFail() {

                hideProgressBar();
            }
        };
    }
}
