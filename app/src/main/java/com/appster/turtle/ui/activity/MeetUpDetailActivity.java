package com.appster.turtle.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.databinding.ActivityMeetUpBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.model.Room;
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
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;

import static com.appster.turtle.ui.Constants.REQUEST_CODE.REQUEST_CREATE_POST;

/**
 * Created  on 15/04/18 .
 */

public class MeetUpDetailActivity extends BaseActivity implements PostListener {

    ActivityMeetUpBinding mBinding;
    private LinearLayoutManager linearLayoutManager;
    private BaseCallback baseCallback;
    private RetrofitRestRepository mRepository;
    private ArrayList<Posts> posts = new ArrayList<>();
    int mCurrentPage = 1, mTotalPagesAvailable = 0;
    private Room room;
    private PostsAdapter postsAdapter;
    private int mVisiblePostItemCount;
    private boolean isPostLoading;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private PostMenuBinding postMenuBottomBinding;

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meet_up);
        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.meetupcap), R.drawable.back_arrow, 0);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        room = getIntent().getExtras().getParcelable(Constants.BundleKey.ROOM);
        postMenuBottomBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.post_menu, mBinding.bottomSheetMembers, false);
        postMenuBottomBinding.tvCancel.setOnClickListener(view -> mBinding.bottomSheetMembers.dismissSheet());

        mBinding.rvNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisiblePostItemCount = linearLayoutManager.getChildCount();
                mTotalPostItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesPostItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesPostItems + mVisiblePostItemCount) >= mTotalPostItemCount && !isPostLoading)) {
                    mCurrentPage++;
                    isPostLoading = true;
                    getPosts();


                }
            }
        });
        mBinding.header.ivIconStart.setOnClickListener(v -> onBackPressed());
        mBinding.rvNotes.setLayoutManager(linearLayoutManager);
        postsAdapter = new PostsAdapter(this, posts, this);
        mBinding.rvNotes.addItemDecoration(new ItemDecorationView(this, Utils.dpToPx(this, 4.8f), Utils.dpToPx(this, 6.7f), Utils.dpToPx(this, 4.8f), Utils.dpToPx(this, 5.8f)));
        mBinding.rvNotes.setAdapter(postsAdapter);
        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            postsAdapter.clear();
            mBinding.swipeRefresh.setRefreshing(true);
            mCurrentPage = 1;
            getPosts();
        });
        getPosts();

    }

    public void getPosts() {
        Observable<PostsResponse> observable;

        observable = mRepository.getApiService().getPosts(room.getRoomId(), mCurrentPage, Constants.LIST_LENGTH, Constants.VIEW_TYPE.MEET_UP, 1);
        new BaseCallback<PostsResponse>(this, observable) {
            @Override
            public void onSuccess(PostsResponse response) {

                isPostLoading = false;
                mBinding.swipeRefresh.setRefreshing(false);
                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (mCurrentPage == 1) {
                    posts.clear();
                    if (response.getData().size() == 0) {
                        mBinding.tvNoUsers.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvNoUsers.setVisibility(View.GONE);
                    }
                }
                posts.addAll(response.getData());
                postsAdapter.notifyItemChanged(posts.size() + 1, posts);


            }

            @Override
            public void onFail() {
                mBinding.swipeRefresh.setRefreshing(false);
                mBinding.tvNoUsers.setVisibility(View.GONE);
                isPostLoading = false;

            }
        };
    }

    public void deletePost(Posts post, final int position) {
        UserActionRequest request = new UserActionRequest();
        request.setRoomId(post.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = mRepository.getApiService().deletePost(request);
        showProgressBar();
        new BaseCallback<UserActionResponse>(this, userActionResponseObservable) {
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

    public void updatePost(Posts post, boolean isFlag, boolean isLike) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlag, isLike);
        }
    }

    @Override
    public void onMenuClicked(final Posts post, final int pos) {
        if (post == null)
            return;

        if (post.isPostedByMe()) {
            postMenuBottomBinding.tvFlag.setVisibility(View.GONE);
            postMenuBottomBinding.tvDelete.setVisibility(View.VISIBLE);
            postMenuBottomBinding.tvEdit.setVisibility(View.VISIBLE);
        } else {
            postMenuBottomBinding.tvFlag.setVisibility(View.VISIBLE);
            postMenuBottomBinding.tvDelete.setVisibility(View.GONE);
            postMenuBottomBinding.tvEdit.setVisibility(View.GONE);
        }

        if (post.isFlaggedByMe()) {

            postMenuBottomBinding.tvFlag.setEnabled(false);
            postMenuBottomBinding.tvFlag.setText(getString(R.string.flaged));
        } else {
            postMenuBottomBinding.tvFlag.setEnabled(true);
            postMenuBottomBinding.tvFlag.setText(getString(R.string.flag_post));
        }

        postMenuBottomBinding.tvFlag.setOnClickListener(v -> {
            //  setFlag(post);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.POST, post);
            bundle.putBoolean(Constants.IS_FROM_POST, true);
            bundle.putInt(Constants.POS, pos);
            ExplicitIntent.getsInstance().navigateForResult(MeetUpDetailActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            mBinding.bottomSheetMembers.dismissSheet();
        });

        postMenuBottomBinding.tvDelete.setOnClickListener(v -> {
            deletePost(post, pos);

            mBinding.bottomSheetMembers.dismissSheet();
        });
        postMenuBottomBinding.tvEdit.setOnClickListener(v -> {
            if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateForResult(MeetUpDetailActivity.this, CreatePostActivity.class, REQUEST_CREATE_POST, bundle);

                mBinding.bottomSheetMembers.dismissSheet();
            }
        });
        mBinding.bottomSheetMembers.showWithSheetView(postMenuBottomBinding.getRoot());
    }

    @Override
    public void onPollClick(int position, int postId, int answerId) {
//
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

    private void likePost(Posts postId, int reaction, int alreadyStatus) {
        showProgressBar();
        new BaseCallback<PostResponse>(this, mRepository.getApiService()
                .likeDislikePost(postId.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                hideProgressBar();
                updatePost(likeDislike.getData(), true, true);
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

        new BaseCallback<Meetup>(this, mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

            @Override
            public void onSuccess(Meetup meetup) {
                postsAdapter.updateMeetupItem(position, meetup.getData());
                hideProgressBar();
            }

            @Override
            public void onFail() {
                hideProgressBar();
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
        ExplicitIntent.getsInstance().navigateForResult(this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.FLAG_POST:
            case REQUEST_CREATE_POST:
                if (resultCode == RESULT_OK) {

                    Posts posts = data.getParcelableExtra(Constants.BundleKey.POST);
                    updatePost(posts, false, false);
                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:
            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:

                if (resultCode == RESULT_OK) {
                    updatePost(data.getExtras().getParcelable(Constants.POST_QUERY), false, false);

                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_REFRESH:
                if (resultCode == RESULT_OK) {
                    mCurrentPage = 1;
                    postsAdapter.clear();
                    getPosts();
                    break;
                }
                break;
            default:
                break;
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
