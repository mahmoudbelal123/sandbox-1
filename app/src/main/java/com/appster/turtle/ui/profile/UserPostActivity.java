package com.appster.turtle.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.databinding.ActivityMyPostBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.HashTagPostDetailActivity;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.ReportActivity;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for user post
 */
public class UserPostActivity extends BaseActivity implements PostListener {

    private PostsAdapter postsAdapter;
    private ArrayList<Posts> posts = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ActivityMyPostBinding binding;
    private int mTotalPostPagesAvailable;
    private boolean isPostLoading;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private int currentPostPage = 1;
    private RetrofitRestRepository mRepository;
    private int userId;
    private PostMenuBinding bottomSheetBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_post);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        userId = getIntent().getIntExtra(Constants.USER_ID, PreferenceUtil.getUser().getUserId());

        setUpHeader(false, binding.clHeader.clHeader, "MY POSTS", "", R.drawable.back_arrow);

        setPostAdapter();

        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(UserPostActivity.this), R.layout.post_menu, binding.bottomSheet, false);
        bottomSheetBinding.tvCancel.setOnClickListener(view -> binding.bottomSheet.dismissSheet());


    }

    @Override
    public String getActivityName() {
        return UserPostActivity.class.getName();
    }

    private void setPostAdapter() {
        postsAdapter = new PostsAdapter(UserPostActivity.this, posts, UserPostActivity.this);
        postsAdapter.setFromRoom(true);
        linearLayoutManager = new LinearLayoutManager(UserPostActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvPost.setLayoutManager(linearLayoutManager);

        binding.rvPost.setAdapter(postsAdapter);

        binding.rvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }


    public void getPosts() {
        Observable<PostsResponse> observable;
        showProgressBar();
        observable = mRepository.getApiService().getUserPost(userId, currentPostPage, Constants.LIST_LENGTH);
        new BaseCallback<PostsResponse>(UserPostActivity.this, observable) {
            @Override
            public void onSuccess(PostsResponse response) {

                hideProgressBar();
                isPostLoading = false;

                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                if (currentPostPage == 1) {
                    posts.clear();

                }
                posts.addAll(response.getData());
                postsAdapter.notifyItemChanged(posts.size() + 1, posts);

                if (posts.isEmpty()) {
                    binding.tvNo.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNo.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFail() {
                isPostLoading = false;
                binding.tvNo.setVisibility(View.GONE);
                hideProgressBar();
                if (posts.isEmpty()) {
                    binding.tvNo.setVisibility(View.VISIBLE);
                }
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
        } else {
            bottomSheetBinding.tvFlag.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvDelete.setVisibility(View.GONE);
            bottomSheetBinding.tvEdit.setVisibility(View.GONE);
        }

        bottomSheetBinding.tvDelete.setOnClickListener(v -> {
            deletePost(post, pos);
            binding.bottomSheet.dismissSheet();
        });

        if (post.getPostType() == Constants.VIEW_TYPE.POLL) {
            bottomSheetBinding.tvEdit.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
        }


        bottomSheetBinding.tvEdit.setOnClickListener(v -> {

            if (post.getPostType() == Constants.VIEW_TYPE.MEDIA) {

                binding.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.POST_QUERY, post);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                ExplicitIntent.getsInstance().navigateForResult(UserPostActivity.this, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);

            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                binding.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateTo(UserPostActivity.this, CreatePostActivity.class, bundle);
            } else {
                Bundle bundle = new Bundle();

                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                ExplicitIntent.getsInstance().navigateTo(UserPostActivity.this, CreatePostActivity.class, bundle);
                binding.bottomSheet.dismissSheet();
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
            ExplicitIntent.getsInstance().navigateForResult(UserPostActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            binding.bottomSheet.dismissSheet();
        });

        binding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());
    }

    public void deletePost(Posts post, final int position) {
        UserActionRequest request = new UserActionRequest();
        request.setRoomId(post.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = mRepository.getApiService().deletePost(request);
        showProgressBar();
        new BaseCallback<UserActionResponse>(UserPostActivity.this, userActionResponseObservable) {
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

    public void updatePost(Posts post, boolean isFlagged, boolean isLike) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlagged, isLike);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:

                    updatePost(data.getExtras().getParcelable(Constants.POST_QUERY), true, false);

                    break;

                case Constants.REQUEST_CODE.FLAG_POST:

                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST), true, false);
                    break;

                case Constants.REQUEST_CODE.REQUEST_ADD_TAG:

                    updatePost(data.getParcelableExtra(Constants.POST_QUERY), true, false);
                    break;

                case Constants.REQUEST_CODE.REQUEST_CREATE_POST:

                    Posts post = data.getParcelableExtra(Constants.BundleKey.POST);
                    updatePost(post, true, false);

                    break;

                case Constants.REQUEST_CODE.REQUEST_REFRESH:
                    currentPostPage = 1;
                    posts.clear();
                    getPosts();
                    break;


            }
        }
    }

    @Override
    public void onPollClick(final int position, int postId, int answerId) {
        showProgressBar();
        new BaseCallback<PostResponse>(UserPostActivity.this, mRepository.getApiService()
                .respondPoll(postId, answerId)) {
            @Override
            public void onSuccess(PostResponse postsResponse) {
                postsAdapter.updateItem(position, postsResponse.getData(), true, false);
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
        likePost(position, postId, likeStatus, alreadyStatus);
    }

    private void likePost(final int pos, final Posts postId, int reaction, int alreadyStatus) {
        showProgressBar();
        new BaseCallback<PostResponse>(this, mRepository.getApiService()
                .likeDislikePost(postId.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                hideProgressBar();
                Posts posts = likeDislike.getData();
                posts.getPostDetail().setShown(postId.getPostDetail().isShown());
                posts.getPostDetail().setChoiceSelected(postId.getPostDetail().isChoiceSelected());
                postsAdapter.updateItem(pos, posts, true, true);

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

        new BaseCallback<Meetup>(UserPostActivity.this, mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

            @Override
            public void onSuccess(Meetup meetup) {
                postsAdapter.updateMeetupItem(position, meetup.getData());
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        LogUtils.LOGD("Log", "Cliked " + string);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.POST, post);
        bundle.putBoolean(Constants.IS_FROM_ROOM, true);
        bundle.putString(Constants.KEY, string);
        ExplicitIntent.getsInstance().navigateForResult(UserPostActivity.this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    public class ItemDecorationView extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;
        private int itemStartOffset;
        private int itemEndOffset;


        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset, int itemStartOffset, int itemEndOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
            this.itemStartOffset = itemStartOffset;
            this.itemEndOffset = itemEndOffset;
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }


            if (itemPosition == 0) {
                outRect.set(itemStartOffset, Utils.dpToPx(UserPostActivity.this, 5), itemEndOffset, itemBottomOffset);

            } else
                outRect.set(itemStartOffset, itemTopOffset, itemEndOffset, itemBottomOffset);
        }

    }

}
