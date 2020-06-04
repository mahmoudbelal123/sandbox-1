package com.appster.turtle.ui.post;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.CommentsAdapter;
import com.appster.turtle.adapter.UserTagAdapter;
import com.appster.turtle.databinding.ActivityPostDetailBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.model.Comment;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.PostCommentRequest;
import com.appster.turtle.network.response.BaseId;
import com.appster.turtle.network.response.GetCommentResponse;
import com.appster.turtle.network.response.GetCommentsResponse;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.HashTagPostDetailActivity;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.ReportActivity;
import com.appster.turtle.ui.home.CommentListener;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.rooms.LikedByActivity;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
/*
* Activity fordeatil post
 */
public class PostDetailActivity extends BaseActivity implements PostListener, View.OnClickListener, CommentListener {
    private ActivityPostDetailBinding mBinding;
    private Posts post;
    private RetrofitRestRepository mRepository;
    private int postId;
    private LinearLayoutManager linearLayoutManager;
    private int mCurrentPage = 1;
    private ArrayList<Comment> comments;
    private CommentsAdapter commentsAdapter;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private boolean isCommentOnly;
    private boolean isPollSelected;
    private PostMenuBinding bottomSheetBinding;
    private Comment commentToModify;
    private int posToModify;
    private String mQuery = "";
    private List<User> mUserTags;
    private UserTagAdapter mRoomTagsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mVisibleTagItemCount;
    private int mTotalTagItemCount;
    private int mPastTagVisiblesItems;
    private int mCurrentTagPage = 1;
    private int mTotalTagPagesAvailable;
    private boolean isTagLoading;
    private BaseCallback<SearchUserResponse> baseCallback;
    private ArrayList<Integer> mSelectedTags = new ArrayList<>();
    private ArrayList<User> mUserSelectedTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(PostDetailActivity.this, R.layout.activity_post_detail);


        postId = getIntent().getExtras().getInt(Constants.POST_ID);
        if (getIntent().hasExtra(Constants.IS_POLL_CHOICE)) {
            isPollSelected = getIntent().getBooleanExtra(Constants.IS_POLL_CHOICE, false);
        }
        isCommentOnly = getIntent().getExtras().getBoolean(Constants.BundleKey.IS_COMMENT);

        mBinding.ivArrow.setOnClickListener(view -> {

        });


        setUpHeader(mBinding.clHeader.clHeader, isCommentOnly ? "COMMENTS" : "", R.drawable.back_arrow, 0);

        ((SimpleItemAnimator) mBinding.rvComments.getItemAnimator()).setSupportsChangeAnimations(false);

        mBinding.rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = linearLayoutManager.getChildCount();
                mTotalItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    mCurrentPage++;

                    isLoading = true;
                    getComments();


                }

            }
        });

        mBinding.etComment.setMentionEnabled(true);
        mBinding.etComment.setHashtagEnabled(false);
        mBinding.etComment.setMentionColor(ContextCompat.getColor(PostDetailActivity.this, R.color.bright_teal));


        getPost();

        mBinding.clHeader.ivIconStart.setOnClickListener(view -> onBackPressed());


        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(PostDetailActivity.this), R.layout.post_menu, mBinding.bottomSheet, false);
        bottomSheetBinding.tvCancel.setOnClickListener(view -> mBinding.bottomSheet.dismissSheet());


        bottomSheetBinding.tvEdit.setVisibility(View.GONE);
        bottomSheetBinding.tvDelete.setText(getString(R.string.delete_comment));
        bottomSheetBinding.tvFlag.setText(getString(R.string.flag_comment));

        bottomSheetBinding.tvFlag.setOnClickListener(this);
        bottomSheetBinding.tvDelete.setOnClickListener(this);


        mBinding.rvHashtagSuggestions.addItemDecoration(new SpaceItemDecoration(12));
        mLinearLayoutManager = new LinearLayoutManager(PostDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvHashtagSuggestions.setLayoutManager(mLinearLayoutManager);

        //load certain number of suggestions at a time, not all at once
        mBinding.rvHashtagSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleTagItemCount = mLinearLayoutManager.getChildCount();
                mTotalTagItemCount = mLinearLayoutManager.getItemCount();
                mPastTagVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentTagPage < mTotalTagPagesAvailable && ((mPastTagVisiblesItems + mVisibleTagItemCount) >= mTotalTagItemCount && !isTagLoading)) {
                    if (mUserTags != null) {
                        mCurrentTagPage++;
                        isLoading = true;
                        getTagSuggestions();

                    }
                }


            }
        });

    }

    @Override
    public String getActivityName() {
        return PostDetailActivity.class.getName();
    }


    private void getPost() {

        mRepository = ((ApplicationController) getApplication()).provideRepository();
        new BaseCallback<PostResponse>(PostDetailActivity.this, mRepository.getApiService()
                .getPost(postId), true) {
            @Override
            public void onSuccess(PostResponse response) {

                if (response != null && response.getData() != null) {
                    post = response.getData();
                    post.getPostDetail().setShown(isPollSelected);
                    initUI();


                    getComments();
                }
            }

            @Override
            public void onFail() {

//
            }
        };

    }

    private void initUI() {

        comments = new ArrayList<>();
        mBinding.clHeader.tvHeaderCenter.setVisibility(View.VISIBLE);
        mBinding.clHeader.tvHeaderCenter.setText(isCommentOnly ? "COMMENTS" : "" + post.getRooms().getValue());

        commentsAdapter = new CommentsAdapter(PostDetailActivity.this, comments, post, this, this, isCommentOnly);
        linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvComments.setLayoutManager(linearLayoutManager);
        mBinding.rvComments.addItemDecoration(new ItemDecoration());

        mBinding.rvComments.setAdapter(commentsAdapter);


        mBinding.etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (mBinding.etComment.getText().toString().isEmpty())
                    mBinding.ivPostComment.setImageResource(R.drawable.ic_send_icon);
                else
                    mBinding.ivPostComment.setImageResource(R.drawable.ic_send_icon_orange);

                checkForHashTags(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });


        mBinding.ivPostComment.setOnClickListener(this);

    }


    private void getComments() {

        new BaseCallback<GetCommentsResponse>(PostDetailActivity.this, mRepository.getApiService()
                .getComments(postId, mCurrentPage, Constants.LIST_LENGTH), true) {
            @Override
            public void onSuccess(GetCommentsResponse response) {

                if (response != null && response.getData() != null) {
                    comments.addAll(response.getData());
                    commentsAdapter.notifyDataSetChanged();
                    mTotalPagesAvailable = response.getPagination().getTotalPages();

                }
                mBinding.tvNoComment.setVisibility(View.GONE);


                if (comments.isEmpty() && isCommentOnly) {
                    mBinding.tvNoComment.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFail() {

                mBinding.tvNoComment.setVisibility(View.GONE);
                if (comments.isEmpty() && isCommentOnly) {
                    mBinding.tvNoComment.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void checkForHashTags(CharSequence s) {
        //for hashtags
        int selection = mBinding.etComment.getSelectionEnd();
//        int selection = s.toString().length();

        String str = s.toString().substring(0, selection);
        if (str.lastIndexOf("@") > str.lastIndexOf(" ")) {
            mQuery = str.substring(str.lastIndexOf("@") + 1, str.length());

            mCurrentTagPage = 1;
            getTagSuggestions();


        } else

        {
            clearTagList();

        }

    }

    private void showRecyclerView() {
        mBinding.rvHashtagSuggestions.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView() {
        mBinding.rvHashtagSuggestions.setVisibility(View.GONE);

    }


    private void clearTagList() {

        hideRecyclerView();

        if (mUserTags != null) {
            mUserTags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }
    }


    private void postComment() {


        hideKeyboard();

        if (mBinding.etComment.getText().toString().length() > 250) {
            return;
        }


        PostCommentRequest commentRequest = new PostCommentRequest();
        commentRequest.setComment(mBinding.etComment.getText().toString());
        commentRequest.setTaggedUserIds(mSelectedTags);
        commentRequest.setPostId(postId);

        new BaseCallback<GetCommentResponse>(PostDetailActivity.this, mRepository.getApiService()
                .postComments(commentRequest), true) {
            @Override
            public void onSuccess(GetCommentResponse response) {

                if (response != null && response.getData() != null) {

                    try {
                        mBinding.etComment.setText("");
                        post.setCommentsCount(post.getCommentsCount() + 1);
                        commentsAdapter.updatePost(post);
                        comments.add(0, response.getData());
                        commentsAdapter.notifyItemInserted(isCommentOnly ? 0 : 1);


                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            //Do something after 100ms
                            mBinding.rvComments.smoothScrollToPosition(0);

                        }, 400);
                    } catch (Exception e) {
                        LogUtils.LOGD(TAG1, e.getMessage());
                    }


                }

                if (comments.isEmpty() && isCommentOnly) {
                    mBinding.tvNoComment.setVisibility(View.VISIBLE);
                } else {
                    mBinding.tvNoComment.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFail() {
//
            }
        };
    }


    private void likeComment(int commentId, int likedStatus, final int position) {

        hideKeyboard();


        new BaseCallback<GetCommentResponse>(PostDetailActivity.this, mRepository.getApiService()
                .likeComment(commentId, likedStatus)) {
            @Override
            public void onSuccess(GetCommentResponse response) {
                if (response != null && response.getData() != null) {
                    mBinding.etComment.setText("");
                    commentsAdapter.updateItem(position, response.getData());
                }


            }

            @Override
            public void onFail() {
//
            }
        };
    }


    @Override
    public void onMenuClicked(Posts post, int room) {
//
    }

    @Override
    public void onPollClick(final int position, int postId, int answerId) {

        new BaseCallback<PostResponse>(PostDetailActivity.this, mRepository.getApiService()
                .respondPoll(postId, answerId)) {
            @Override
            public void onSuccess(PostResponse postsResponse) {

                if (postsResponse != null && postsResponse.getData() != null) {
                    post = postsResponse.getData();
                    commentsAdapter.updateItem(position, postsResponse.getData());
                }

            }

            @Override
            public void onFail() {
                //
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
        likePost(postId, likeStatus, alreadyStatus, position);
    }

    private void likePost(Posts postId, int reaction, int alreadyStatus, final int position) {
        showProgressBar();
        new BaseCallback<PostResponse>(this, mRepository.getApiService()
                .likeDislikePost(postId.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                hideProgressBar();


                likeDislike.getData().getPostDetail().setShown(post.getPostDetail().isShown());
                likeDislike.getData().getPostDetail().setChoiceSelected(post.getPostDetail().isChoiceSelected());
                post = likeDislike.getData();
                commentsAdapter.updatePost(post);
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

        new BaseCallback<Meetup>(PostDetailActivity.this, mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

            @Override
            public void onSuccess(Meetup meetup) {
                if (meetup != null && meetup.getData() != null) {
                    post = meetup.getData();
                    commentsAdapter.updateMeetupItem(position, meetup.getData());
                }
            }

            @Override
            public void onFail() {
                //
            }
        };
    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.POST, post);
        bundle.putBoolean(Constants.IS_FROM_ROOM, false);
        bundle.putString(Constants.KEY, string);
        ExplicitIntent.getsInstance().navigateForResult(PostDetailActivity.this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);

    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_post_comment:
                if (mBinding.etComment.getText().toString().isEmpty())
                    return;

                postComment();
                break;
            case R.id.tv_delete:
                mBinding.bottomSheet.dismissSheet();
                deleteComment();
                break;
            default:
                break;
        }
    }

    private void deleteComment() {

        BaseId baseId = new BaseId(commentToModify.getCommentId());

        new BaseCallback<BaseResponse>(PostDetailActivity.this, mRepository.getApiService().deleteComment(baseId)) {

            @Override
            public void onSuccess(BaseResponse response) {

                if (response != null) {
                    post.setCommentsCount(post.getCommentsCount() - 1);
                    commentsAdapter.updatePost(post);
                    commentsAdapter.deleteItem(posToModify, commentToModify);

                }
            }

            @Override
            public void onFail() {
                //
            }
        };


    }


    @Override
    public void onCommentMenuClicked(final Comment comment, final int pos) {

        this.commentToModify = comment;
        this.posToModify = pos;
        if (comment.getCreatedBy().getUserId() == PreferenceUtil.getUser().getUserId()) {
            bottomSheetBinding.tvDelete.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvFlag.setVisibility(View.GONE);
        } else {
            bottomSheetBinding.tvDelete.setVisibility(View.GONE);
            bottomSheetBinding.tvFlag.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvFlag.setEnabled(true);


            if (comment.isFlaggedByMe()) {

                bottomSheetBinding.tvFlag.setEnabled(false);
                bottomSheetBinding.tvFlag.setText(getString(R.string.flaged));
            } else {
                bottomSheetBinding.tvFlag.setEnabled(true);
                bottomSheetBinding.tvFlag.setText(getString(R.string.flag_comment));
            }

            bottomSheetBinding.tvFlag.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.COMMENT, comment);
                bundle.putBoolean(Constants.IS_FROM_COMMENT, true);
                bundle.putInt(Constants.POS, posToModify);
                ExplicitIntent.getsInstance().navigateForResult(PostDetailActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
                mBinding.bottomSheet.dismissSheet();
            });
        }


        mBinding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());

    }

    @Override
    public void onCommentLiked(int position, int postId, int likeStatus) {

        likeComment(postId, likeStatus, position);
    }

    @Override
    public void onCommentLikedBy(int position, int commentId, int likeStatus) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.POST, postId);
        bundle.putInt(Constants.BundleKey.COMMENT, commentId);
        ExplicitIntent.getsInstance().navigateTo(PostDetailActivity.this, LikedByActivity.class, bundle);

    }

    public void getTagSuggestions() {

        showRecyclerView();
        Observable observable;
        if (mQuery.isEmpty()) {
            observable = mRepository.getApiService()
                    .getMembers(post.getRooms().getId(), mCurrentTagPage, Constants.LIST_LENGTH, mQuery);
        } else {
            observable = mRepository.getApiService()
                    .searchUser(mCurrentTagPage, Constants.LIST_LENGTH, mQuery);
        }

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<SearchUserResponse>(PostDetailActivity.this, observable) {
            @Override
            public void onSuccess(SearchUserResponse response) {

                if (mCurrentTagPage == 1) {
                    mUserTags = response.getData();
                    mTotalTagPagesAvailable = response.getPagination().getTotalPages();
                    isTagLoading = false;
                    mRoomTagsAdapter = new UserTagAdapter(PostDetailActivity.this, response.getData());
                    mBinding.rvHashtagSuggestions.setAdapter(mRoomTagsAdapter);

                } else {
                    mTotalTagPagesAvailable = response.getPagination().getTotalPages();
                    mUserTags.addAll(response.getData());
                    isTagLoading = false;
                    mRoomTagsAdapter.notifyDataSetChanged();
                }

                User deleteUser = null;
                for (User user : mUserTags) {
                    if (user.getUserId() == PreferenceUtil.getUser().getUserId()) {
                        deleteUser = user;
                    }
                }

                if (deleteUser != null) {
                    mUserTags.remove(deleteUser);
                    mRoomTagsAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    public void appendTag(User tag) {

        String originalString = mBinding.etComment.getText().toString().trim();

        String endString = originalString.substring(mBinding.etComment.getSelectionEnd(), originalString.length());

        String startString = originalString.substring(0, mBinding.etComment.getSelectionEnd());
        startString = startString.substring(0, startString.lastIndexOf("@") + 1);

        String finalString = startString + tag.getUserName().toLowerCase() + endString;
        if (!endString.isEmpty() && endString.charAt(0) != ' ') {
            finalString = startString + tag.getUserName().toLowerCase() + " " + endString;
        }


        mBinding.etComment.setText(finalString);
        mBinding.etComment.setSelection((startString + tag.getUserName().toLowerCase()).length());


        if (!mSelectedTags.contains(tag.getUserId()))
            mSelectedTags.add(tag.getUserId());

        if (!mUserSelectedTags.contains(tag))
            mUserSelectedTags.add(tag);

        mBinding.etComment.setTagList(mUserSelectedTags);

        clearTagList();

    }

    public class ItemDecoration extends RecyclerView.ItemDecoration {


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            final int itemPosition = parent.getChildAdapterPosition(view);

            final int itemCount = state.getItemCount();
            if (itemCount > 0 && itemPosition == itemCount) {
                outRect.set(0, 0, 0, 0);

            }

        }

    }

    Spannable mspanable;
    int hashTagIsComing = 0;


    private void tagCheck(String s, int start, int end) {

        if (start == end) {
            Typeface fontBold = Typeface.createFromAsset(getAssets(), getString(R.string.eina_01_regular));
            mspanable.setSpan(new CustomTypefaceSpan("", fontBold), start, start + s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


        } else {
            Typeface fontBold = Typeface.createFromAsset(getAssets(), getString(R.string.eina_01_semibold));
            mspanable.setSpan(new CustomTypefaceSpan("", fontBold), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        if (post != null) {
            post.getPostDetail().setChoiceSelected(false);
            intent.putExtra(Constants.POST_QUERY, post);
            setResult(RESULT_OK, intent);
        } else
            setResult(RESULT_CANCELED);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.FLAG_POST:
                if (resultCode == RESULT_OK) {

                    Comment comment = data.getParcelableExtra(Constants.BundleKey.COMMENT);
                    commentsAdapter.updateItem(posToModify, comment);
                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:

                if (resultCode == RESULT_OK) {
                    post = data.getExtras().getParcelable(Constants.POST_QUERY);
                    initUI();

                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_REFRESH:
                getPost();
                break;

            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
