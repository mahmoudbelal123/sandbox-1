package com.appster.turtle.ui.rooms;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.databinding.FragmentRoomLayoutBinding;
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
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeFeedFragment;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created  on 11/03/18 .
 *
 * fragment for room
 */

public class RoomFragment extends BaseFragment implements PostListener {

    private static final String SCREEN = "screen";
    private static final String ROOM = "room";


    private int mScreen;


    private OnFragmentInteractionListener mListener;
    private LinearLayoutManager linearLayoutManager;
    private FragmentRoomLayoutBinding binding;
    private RetrofitRestRepository mRepository;
    private int currentPostPage = 1;
    private Room mRoom;
    private ArrayList<Posts> posts = new ArrayList<>();
    private PostsAdapter postsAdapter;
    private boolean isDataRefreshed;

    private int mTotalPostPagesAvailable;
    private boolean isPostLoading;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;


    public RoomFragment() {
        //
    }

    public static RoomFragment newInstance(int screen, Room room) {
        RoomFragment fragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putInt(SCREEN, screen);
        args.putParcelable(ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentName() {
        return HomeFeedFragment.class.getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreen = getArguments().getInt(SCREEN);
            mRoom = getArguments().getParcelable(ROOM);
        }


    }

    public void dataRefresh() {

        currentPostPage = 1;
        isPostLoading = true;
        posts.clear();
        if (postsAdapter != null)
            postsAdapter.notifyDataSetChanged();

        if (!isHidden()) {
            getPosts();
        } else
            isDataRefreshed = true;


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isDataRefreshed) {
            getPosts();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room_layout, container, false);
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        setPostAdapter();


        String string = binding.tvEmpty.getText().toString();

        String text = "text";
        String image = "image";
        String meetup = "meetup";

        SpannableString content = new SpannableString(binding.tvEmpty.getText());
        content.setSpan(new UnderlineSpan(), string.indexOf(text), string.indexOf(text) + text.length(), 0);
        content.setSpan(new UnderlineSpan(), string.indexOf(image), string.indexOf(image) + image.length(), 0);
        content.setSpan(new UnderlineSpan(), string.indexOf(meetup), string.indexOf(meetup) + meetup.length(), 0);
        binding.tvEmpty.setText(content);

        ((SimpleItemAnimator) binding.rvPost.getItemAnimator()).setSupportsChangeAnimations(false);


        binding.ivAdd.setOnClickListener(view -> {

            if (getActivity() instanceof RoomActivityCoordinator) {
                if (getActivity() != null) {
                    if (mRoom.isUserAdmin()) {
                        ((RoomActivityCoordinator) (getActivity())).initPost();
                    } else {
                        if ((mRoom.getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED && mRoom.isMemberCanPost()))
                            ((RoomActivityCoordinator) (getActivity())).initPost();
                        else
                            return;
                    }
                }
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMenuClicked(Posts post, int pos) {
//
    }

    @Override
    public void onPollClick(final int position, int postId, int answerId) {
        showProgressBar();
        new BaseCallback<PostResponse>((BaseActivity) getActivity(), mRepository.getApiService()
                .respondPoll(postId, answerId)) {
            @Override
            public void onSuccess(PostResponse postsResponse) {
                postsAdapter.updateItem(position, postsResponse.getData(), false, false);
                hideHud();
            }

            @Override
            public void onFail() {
                //  setRefreshing(false);
            }
        };
    }

    @Override
    public void onLikedBy(int position, int postId) {
//
    }

    @Override
    public void onLiked(int position, Posts post, int likeStatus, int alreadyStatus) {
        likePost(post, likeStatus, alreadyStatus);
    }

    @Override
    public void onClick(Posts post) {
//
    }

    @Override
    public void onMeetupStatusChange(final int position, int postId, int status, boolean isMeetupResponded) {
        showProgressBar();

        new BaseCallback<Meetup>((BaseActivity) getActivity(), mRepository.getApiService().meetupStatusChanged(postId, status, isMeetupResponded)) {

            @Override
            public void onSuccess(Meetup meetup) {
                postsAdapter.updateMeetupItem(position, meetup.getData());
                hideHud();
            }

            @Override
            public void onFail() {
                hideHud();
            }
        };
    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        LogUtils.LOGD("Log", "Cliked 12" + string);
        ((RoomActivityCoordinator) getActivity()).onHasHTagClick(post, pos, string);
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void setPostAdapter() {
        binding.rvRooms.setVisibility(View.GONE);
        postsAdapter = new PostsAdapter(getActivity(), posts, (RoomActivityCoordinator) getActivity());
        postsAdapter.setFromRoom(true);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPost.setLayoutManager(linearLayoutManager);
        binding.rvPost.setAdapter(postsAdapter);
//binding.rvPost.addItemDecoration(new ItemDecorationViewFrg(getActivity() , 0 , Utils.dpToPx(getActivity() , 7.7f) , 0 ,0));
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
        if (currentPostPage == 1) {
            if (getBaseActivity() instanceof RoomActivityCoordinator && getBaseActivity() != null && !((RoomActivityCoordinator) getBaseActivity()).isSwipeRefreshing())
                binding.loading.setVisibility(View.VISIBLE);
        }
        Observable<PostsResponse> observable;

        observable = mRepository.getApiService().getPostsByFilter(mRoom.getRoomId(), currentPostPage, Constants.LIST_LENGTH, mScreen);
        new BaseCallback<PostsResponse>(getBaseActivity(), observable) {
            @Override
            public void onSuccess(PostsResponse response) {

                binding.loading.setVisibility(View.GONE);

                if (currentPostPage == 1)
                    posts.clear();

                if (getBaseActivity() != null && ((RoomActivityCoordinator) getBaseActivity()).isSwipeRefreshing())
                    ((RoomActivityCoordinator) getBaseActivity()).hideSwipeRefresh();

                isPostLoading = false;

                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                posts.addAll(response.getData());
                postsAdapter.notifyItemChanged(posts.size() + 1, posts);


                if (posts.isEmpty())
                    binding.cvEmpty.setVisibility(View.VISIBLE);
                else
                    binding.cvEmpty.setVisibility(View.GONE);


            }

            @Override
            public void onFail() {
                isPostLoading = false;
                binding.loading.setVisibility(View.GONE);

                if (posts.isEmpty())
                    binding.cvEmpty.setVisibility(View.VISIBLE);
                else
                    binding.cvEmpty.setVisibility(View.GONE);
            }
        };
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


            /* first position */
            if (itemPosition == 0) {
                outRect.set(itemStartOffset, Utils.dpToPx(getActivity(), 5), itemEndOffset, itemBottomOffset);

            } else
                outRect.set(itemStartOffset, itemTopOffset, itemEndOffset, itemBottomOffset);
        }

    }

    public void deletePost(Posts post, final int position) {
        UserActionRequest request = new UserActionRequest();
        request.setRoomId(post.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = mRepository.getApiService().deletePost(request);
        showProgressBar();
        new BaseCallback<UserActionResponse>((BaseActivity) getActivity(), userActionResponseObservable) {
            @Override
            public void onSuccess(UserActionResponse response) {
                hideHud();
                getBaseActivity().showSnackBar(getString(R.string.poste_deleted));
                postsAdapter.removeItem(position);
            }

            @Override
            public void onFail() {
                hideHud();

            }
        };
    }

    public void updatePost(Posts post, boolean isFlagged, boolean isLikeStatus) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlagged, isLikeStatus);
        } else {
            dataRefresh();
        }
    }

    private void likePost(final Posts post, int reaction, int alreadyStatus) {
        showProgressBar();
        new BaseCallback<PostResponse>((BaseActivity) getActivity(), mRepository.getApiService()
                .likeDislikePost(post.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                hideHud();
                Posts data = likeDislike.getData();
                data.getPostDetail().setShown(post.getPostDetail().isShown());
                data.getPostDetail().setChoiceSelected(post.getPostDetail().isChoiceSelected());
                updatePost(data, true, true);
            }

            @Override
            public void onFail() {
                hideHud();
            }
        };
    }


}
