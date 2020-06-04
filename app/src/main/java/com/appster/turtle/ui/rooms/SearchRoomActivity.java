package com.appster.turtle.ui.rooms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.databinding.ActivitySearchRoomBinding;
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

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for search room
 */
public class SearchRoomActivity extends BaseActivity implements View.OnClickListener, PostListener {

    private ActivitySearchRoomBinding mBinder;
    private RetrofitRestRepository mRepository;
    private Handler handler = new Handler();
    private int currentPostPage = 1;
    private boolean isPostLoading;
    private int mTotalPostPagesAvailable;
    private ArrayList<Posts> posts = new ArrayList();
    private PostsAdapter postsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private String query = "";
    private int roomId;
    private int filter;
    private PostMenuBinding bottomSheetBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(SearchRoomActivity.this, R.layout.activity_search_room);

        roomId = getIntent().getIntExtra(Constants.BundleKey.ROOM_ID, 0);
        filter = getIntent().getIntExtra(Constants.FILTER, 0);

        init();
    }

    @Override
    public String getActivityName() {
        return SearchRoomActivity.class.getName();
    }

    private void init() {
        mRepository = ((ApplicationController) this.getApplicationContext()).provideRepository();

        postsAdapter = new PostsAdapter(SearchRoomActivity.this, posts, SearchRoomActivity.this);
        postsAdapter.setFromRoom(true);
        linearLayoutManager = new LinearLayoutManager(SearchRoomActivity.this, LinearLayoutManager.VERTICAL, false);
        mBinder.rvPost.setLayoutManager(linearLayoutManager);


        mBinder.rvPost.setAdapter(postsAdapter);

        mBinder.rvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        mBinder.ivClear.setOnClickListener(this);
        mBinder.ivBack.setOnClickListener(this);

        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(SearchRoomActivity.this), R.layout.post_menu, mBinder.bottomSheet, false);
        bottomSheetBinding.tvCancel.setOnClickListener(view -> mBinder.bottomSheet.dismissSheet());


        mBinder.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0)
                    mBinder.ivClear.setVisibility(View.INVISIBLE);
                else
                    mBinder.ivClear.setVisibility(View.VISIBLE);


                handler.postDelayed(() -> {
                    currentPostPage = 1;
                    query = charSequence.toString();
                    postsAdapter.setQuery(query);

                    getPosts();

                }, 200);
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });

    }

    private void getPosts() {


        Observable<PostsResponse> observable;

        if (query.isEmpty()) {
            posts.clear();
            postsAdapter.notifyDataSetChanged();
            return;
        }

        observable = mRepository.getApiService().getSearchPost(roomId, currentPostPage, Constants.LIST_LENGTH, filter, 0, query);
        new BaseCallback<PostsResponse>(SearchRoomActivity.this, observable) {
            @Override
            public void onSuccess(PostsResponse response) {

                isPostLoading = false;

                if (currentPostPage == 1) {
                    posts.clear();
                    postsAdapter.notifyDataSetChanged();

                }


                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                posts.addAll(response.getData());
                postsAdapter.notifyItemChanged(posts.size() + 1, posts);

                if (mBinder.etSearch.getText().toString().isEmpty())
                    postsAdapter.clear();


                mBinder.tvHint.setVisibility(posts.isEmpty() ? View.VISIBLE : View.INVISIBLE);

            }

            @Override
            public void onFail() {
                isPostLoading = false;

                if (mBinder.etSearch.getText().toString().isEmpty())
                    postsAdapter.clear();

                mBinder.tvHint.setVisibility(posts.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
        };
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_clear:
                mBinder.etSearch.setText("");

                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            default:
                break;
        }
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
            mBinder.bottomSheet.dismissSheet();
        });
        bottomSheetBinding.tvEdit.setOnClickListener(v -> {
            if (post.getPostType() == Constants.VIEW_TYPE.MEDIA) {
//                    showSnackBar("In progress");
                mBinder.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putParcelable(Constants.POST_QUERY, post);
                ExplicitIntent.getsInstance().navigateForResult(SearchRoomActivity.this, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);
//
            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateForResult(SearchRoomActivity.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);

                mBinder.bottomSheet.dismissSheet();
            } else {
                Bundle bundle = new Bundle();

                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                ExplicitIntent.getsInstance().navigateForResult(SearchRoomActivity.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);
                mBinder.bottomSheet.dismissSheet();
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
            ExplicitIntent.getsInstance().navigateForResult(SearchRoomActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            mBinder.bottomSheet.dismissSheet();
        });

        mBinder.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());
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

        new BaseCallback<Meetup>(SearchRoomActivity.this, mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

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
      //  LogUtils.LOGD("Log", "Cliked " + string);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.POST, post);
        bundle.putBoolean(Constants.IS_FROM_ROOM, true);
        bundle.putString(Constants.KEY, string);
        ExplicitIntent.getsInstance().navigateForResult(SearchRoomActivity.this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);

    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    @Override
    public void onPollClick(final int position, int postId, int answerId) {
        showProgressBar();
        new BaseCallback<PostResponse>(SearchRoomActivity.this, mRepository.getApiService()
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


    public void deletePost(Posts post, final int position) {
        UserActionRequest request = new UserActionRequest();
        request.setRoomId(post.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = mRepository.getApiService().deletePost(request);
        showProgressBar();
        new BaseCallback<UserActionResponse>(SearchRoomActivity.this, userActionResponseObservable) {
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

    public void updatePost(Posts post, boolean isFlaged, boolean isLikeStatus) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlaged, isLikeStatus);
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

                case Constants.REQUEST_CODE.REQUEST_ADD_TAG:

                    if (resultCode == RESULT_OK) {
                        refreshData();

                        break;
                    }
                    break;

                case Constants.REQUEST_CODE.REQUEST_CREATE_POST:


                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST), true, false);
                    break;

                case Constants.REQUEST_CODE.FLAG_POST:


                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST), true, false);
                    break;
                case Constants.REQUEST_CODE.REQUEST_REFRESH:
                    refreshData();
                    break;


            }
        }
    }

    private void refreshData() {
        getPosts();
    }

}
