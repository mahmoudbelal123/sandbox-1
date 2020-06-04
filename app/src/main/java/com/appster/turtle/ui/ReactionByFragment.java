package com.appster.turtle.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MembersAdapter;
import com.appster.turtle.adapter.viewholder.MembersViewHolder;
import com.appster.turtle.databinding.FragmentReactionLayoutBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.LikedByResponse;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Created  on 26/04/18 .
 * frgamnet for reaction by
 */

public class ReactionByFragment extends BaseFragment implements MembersViewHolder.IMembers {
    private static final String REACTION_ID = "reaction_id";
    private static final String POST_ID = "postId";
    private FragmentReactionLayoutBinding mBinding;
    int postId, reactionId;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private RetrofitRestRepository repository;
    private ArrayList<User> data = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MembersAdapter mAdapter;

    @Override
    public String getFragmentName() {
        return getClass().getSimpleName();
    }

    public static ReactionByFragment newInstance(int sectionNumber, int postId) {
        ReactionByFragment fragment = new ReactionByFragment();
        Bundle b = new Bundle();
        b.putInt(REACTION_ID, sectionNumber);
        b.putInt(POST_ID, postId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reaction_layout, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new MembersAdapter(getActivity(), data, (BaseFragment) ReactionByFragment.this);
        postId = getArguments().getInt(POST_ID);
        reactionId = getArguments().getInt(REACTION_ID);
        mBinding.rvReaction.setLayoutManager(mLinearLayoutManager);
        mBinding.rvReaction.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 25)));

        mBinding.rvReaction.setAdapter(mAdapter);

        mBinding.rvReaction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                int mPastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = mLinearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    mCurrentPage++;
                    isLoading = true;
                    getReaction();
                }
            }
        });
        getReaction();

        mBinding.swipeReaction.setOnRefreshListener(() -> {
            mBinding.swipeReaction.setRefreshing(true);
            mCurrentPage = 1;
            getReaction();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            mCurrentPage = 1;
            getReaction();
        }
    }

    private void getReaction() {
        if (!mBinding.swipeReaction.isRefreshing())
            showProgressBar();

        new BaseCallback<LikedByResponse>((BaseActivity) getActivity(), repository.getApiService().getLikedReactionBy(postId, reactionId, mCurrentPage, Constants.LIST_LENGTH)) {

            @Override
            public void onSuccess(LikedByResponse response) {
                mBinding.swipeReaction.setRefreshing(false);

                if (response != null && response.getData() != null) {
                    if (mCurrentPage == 1) {
                        data.clear();
                    }

                    data.addAll(response.getData());
                    mBinding.txtCount.setText(getResources().getQuantityString(R.plurals.reactionCount, response.getPagination().getNumberOfElements(), response.getPagination().getNumberOfElements()));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail() {
                mBinding.swipeReaction.setRefreshing(false);
                isLoading = false;
            }
        };

    }

    @Override
    public void onMemberSelected(int userId, int pos) {
//
    }

    @Override
    public void onMemberAccept(int userId) {
//
    }

    @Override
    public void onMemberDecline(int userId) {
//
    }
}
