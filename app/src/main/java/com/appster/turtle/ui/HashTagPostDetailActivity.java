package com.appster.turtle.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.databinding.ActivityRoomHashNewBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created  on 29/03/18 .
 */

public class HashTagPostDetailActivity extends BaseActivity implements PostListener {

    private ActivityRoomHashNewBinding mBinding;
    private ArrayList<Posts> posts = new ArrayList<>();
    private Posts mPost;
    private String hashSearchString;
    private PostsAdapter postsAdapter;
    private boolean isDataRefreshed;

    private int mTotalPostPagesAvailable;
    private boolean isPostLoading;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private RetrofitRestRepository mRepository;
    private int currentPostPage = 1;
    private int roomId;
    private int SCREEN;
    private LinearLayoutManager linearLayoutManager;
    private PostMenuBinding bottomSheetBinding;


    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_hash_new);
        String stringExtra = getIntent().getStringExtra(Constants.KEY);
        if (stringExtra.startsWith("#")) {
            hashSearchString = stringExtra.substring(1);
        } else {
            hashSearchString = stringExtra;
        }
        SCREEN = getIntent().getIntExtra(Constants.POS, 3);
        mPost = getIntent().getParcelableExtra(Constants.BundleKey.POST);
        roomId = mPost.getRooms().getId();
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    private void init() {
        setUpHeader(true
                , mBinding.header.clHeader, hashSearchString.toUpperCase());

        mBinding.header.ivIconStart.setOnClickListener(v -> onBackPressed());
        mBinding.header.tvHeaderCenter.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        mBinding.header.tvHeaderCenter.setMaxLines(1);
        mBinding.header.tvHeaderCenter.setEllipsize(TextUtils.TruncateAt.END);
        ViewGroup.LayoutParams layoutParams = mBinding.header.tvHeaderCenter.getLayoutParams();
        layoutParams.width = Utils.dpToPx(this, 200);
        postsAdapter = new PostsAdapter(this, posts, this);
        postsAdapter.setFromRoom(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvPost.setLayoutManager(linearLayoutManager);


        mBinding.rvPost.setAdapter(postsAdapter);

        mBinding.rvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisiblePostItemCount = linearLayoutManager.getChildCount();
                mTotalPostItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesPostItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (currentPostPage < mTotalPostPagesAvailable && ((mPastVisiblesPostItems + mVisiblePostItemCount) >= mTotalPostItemCount && !isPostLoading)) {
                    currentPostPage++;
                    isPostLoading = true;
                    getPosts();
                }
            }
        });

        getPosts();


        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);
            currentPostPage = 1;
            isDataRefreshed = true;
            getPosts();
        });

        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(HashTagPostDetailActivity.this), R.layout.post_menu, mBinding.bottomSheet, false);
        bottomSheetBinding.tvCancel.setOnClickListener(view -> mBinding.bottomSheet.dismissSheet());
    }

    public void getPosts() {
        if (currentPostPage == 1) {
            if (!isDataRefreshed)
                mBinding.loading.setVisibility(View.VISIBLE);
        }

        Observable<PostsResponse> observable;

        observable = mRepository.getApiService().getPosts(roomId, currentPostPage, Constants.LIST_LENGTH, hashSearchString, SCREEN);
        new BaseCallback<PostsResponse>(this, observable) {
            @Override
            public void onSuccess(PostsResponse response) {

                mBinding.loading.setVisibility(View.GONE);
                mBinding.swipeRefresh.setRefreshing(false);
                isPostLoading = false;
                isDataRefreshed = false;
                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                if (currentPostPage == 1) {
                    posts.clear();
                }
                posts.addAll(response.getData());
                postsAdapter.notifyItemChanged(posts.size() + 1, posts);
            }

            @Override
            public void onFail() {
                isPostLoading = false;
                mBinding.loading.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onMenuClicked(final Posts post, final int pos) {

        hideKeyboard();

        if (post == null)
            return;

        if (post.isPostedByMe()) {
            bottomSheetBinding.tvFlag.setVisibility(View.GONE);
            bottomSheetBinding.tvDelete.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
            if (post.getPostType() == Constants.VIEW_TYPE.POLL) {
                bottomSheetBinding.tvEdit.setVisibility(View.GONE);
            } else {
                bottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
            }
        } else {
            bottomSheetBinding.tvFlag.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvDelete.setVisibility(View.GONE);
            bottomSheetBinding.tvEdit.setVisibility(View.GONE);
        }

        bottomSheetBinding.tvDelete.setOnClickListener(v -> {
            deletePost(post, pos);
            mBinding.bottomSheet.dismissSheet();
        });


        bottomSheetBinding.tvEdit.setOnClickListener(v -> {

            if (post.getPostType() == Constants.VIEW_TYPE.MEDIA) {
                mBinding.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.POST_QUERY, post);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                ExplicitIntent.getsInstance().navigateForResult(HashTagPostDetailActivity.this, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);

                mBinding.bottomSheet.dismissSheet();
            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateForResult(HashTagPostDetailActivity.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);

                mBinding.bottomSheet.dismissSheet();
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                ExplicitIntent.getsInstance().navigateForResult(HashTagPostDetailActivity.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);
                mBinding.bottomSheet.dismissSheet();
            }
        });
        if (post.isFlaggedByMe()) {
            bottomSheetBinding.tvFlag.setEnabled(false);
            bottomSheetBinding.tvFlag.setText(getString(R.string.flaged));
        } else {
            bottomSheetBinding.tvFlag.setEnabled(true);
            bottomSheetBinding.tvFlag.setText(getString(R.string.flag_post));
        }
        bottomSheetBinding.tvFlag.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.POST, post);
            bundle.putBoolean(Constants.IS_FROM_POST, true);
            bundle.putInt(Constants.POS, pos);
            ExplicitIntent.getsInstance().navigateForResult(HashTagPostDetailActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            mBinding.bottomSheet.dismissSheet();
        });

        mBinding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());
    }

    public void deletePost(Posts post, final int position) {
        UserActionRequest request = new UserActionRequest();
        request.setRoomId(post.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = mRepository.getApiService().deletePost(request);
        showProgressBar();
        new BaseCallback<UserActionResponse>(HashTagPostDetailActivity.this, userActionResponseObservable) {
            @Override
            public void onSuccess(UserActionResponse response) {
                hideProgressBar();
                showSnackBar("Post deleted successfully");
                postsAdapter.removeItem(position);
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };
    }

    @Override
    public void onPollClick(final int position, int postId, int answerId) {

        showProgressBar();
        new BaseCallback<PostResponse>(HashTagPostDetailActivity.this, mRepository.getApiService()
                .respondPoll(postId, answerId)) {
            @Override
            public void onSuccess(PostResponse postsResponse) {
                postsAdapter.updateItem(position, postsResponse.getData(), false, false);
                hideProgressBar();
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    @Override
    public void onLikedBy(int position, int postId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.POST, postId);
        ExplicitIntent.getsInstance().navigateTo(this, ReactionByActivity.class, bundle);
    }

    @Override
    public void onLiked(int position, Posts postId, int likeStatus, int alreadyStatus) {
        likePost(postId, likeStatus, alreadyStatus);
    }

    private void likePost(final Posts postId, int reaction, int alreadyStatus) {
        showProgressBar();
        new BaseCallback<PostResponse>(this, mRepository.getApiService()
                .likeDislikePost(postId.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                hideProgressBar();
                Posts data = likeDislike.getData();
                data.getPostDetail().setShown(postId.getPostDetail().isShown());
                data.getPostDetail().setChoiceSelected(postId.getPostDetail().isChoiceSelected());
                updatePost(data, true, true);

            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    @Override
    public void onClick(Posts post) {
//
    }

    @Override
    public void onMeetupStatusChange(final int position, int postId, int status, boolean isMeetupResponded) {

        showProgressBar();

        new BaseCallback<Meetup>(HashTagPostDetailActivity.this, mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

            @Override
            public void onSuccess(Meetup meetup) {
                hideProgressBar();
                postsAdapter.updateMeetupItem(position, meetup.getData());
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        if (string.startsWith("#")) {
            hashSearchString = string.substring(1);
        } else {
            hashSearchString = string;
        }
        mBinding.header.tvHeaderCenter.setText(hashSearchString);
        mBinding.header.tvHeaderCenter.setAllCaps(true);
        currentPostPage = 1;
        postsAdapter.clear();
        getPosts();
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:
                    Posts posts = data.getParcelableExtra(Constants.POST_QUERY);
                    updatePost(posts, true, false);
                    break;

                case Constants.REQUEST_CODE.FLAG_POST:


                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST), true, false);
                    break;

                case Constants.REQUEST_CODE.REQUEST_CREATE_POST:


                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST), true, false);
                    break;

                case Constants.REQUEST_CODE.REQUEST_ADD_TAG:
                    updatePost(data.getParcelableExtra(Constants.POST_QUERY), true, false);
                    break;

                default:
                    break;
            }
        }
    }

    public void updatePost(Posts post, boolean isFlaged, boolean isLiked) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlaged, isLiked);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    public void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }
}
