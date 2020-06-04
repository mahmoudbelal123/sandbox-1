package com.appster.turtle.ui.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PostsAdapter;
import com.appster.turtle.adapter.RoomCardAdapter;
import com.appster.turtle.databinding.FragmentHomeFeedBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;
/**
 * A activity class for home feed
 */
public class HomeFeedFragment extends BaseFragment implements PostListener {
    private static final String IS_HOME = "IS_HOME";
    private static final String SCREEN = "screen";

    private boolean isHome;
    private int mScreen;


    private OnFragmentInteractionListener mListener;
    private LinearLayoutManager roomLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private FragmentHomeFeedBinding binding;
    private RetrofitRestRepository mRepository;
    private int currentRoomPage = 1;
    private int currentPostPage = 1;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Posts> posts = new ArrayList<>();
    private RoomCardAdapter roomCardAdapter;
    private PostsAdapter postsAdapter;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mTotalPagesAvailable;
    private boolean isRoomLoading;
    private boolean isDataRefreshed;
    private boolean isSwipeToRefresh;

    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private int mTotalPostPagesAvailable;
    private boolean isPostLoading;


    public HomeFeedFragment() {
        //
    }

    public static HomeFeedFragment newInstance(boolean isHome, int screen) {
        HomeFeedFragment fragment = new HomeFeedFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_HOME, isHome);
        args.putInt(SCREEN, screen);
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
            isHome = getArguments().getBoolean(IS_HOME);
            mScreen = getArguments().getInt(SCREEN);
        }
    }

    public void dataRefresh() {
        currentRoomPage = 1;
        isRoomLoading = true;
        currentPostPage = 1;
        isPostLoading = true;
        rooms.clear();
        posts.clear();

        if (postsAdapter != null)
            postsAdapter.notifyDataSetChanged();
        if (roomCardAdapter != null)
            roomCardAdapter.notifyDataSetChanged();

        if (!isHidden()) {
            //getRooms();
            getPosts();
        } else
            isDataRefreshed = true;


    }

    public void dataRoomRefresh(int pos) {
        if (roomCardAdapter != null) {
            roomCardAdapter.remove(pos);
        }

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
          //  getRooms();
            getPosts();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_feed, container, false);

        binding.tv.setText((isHome ? "ISHOME" : "EXPLORE") + " SCREEN" + mScreen);
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();


        setRoomAdapter();
        setPostAdapter();
        getRooms();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(true);
            currentPostPage = 1;
            currentRoomPage = 1;
            isSwipeToRefresh = true;
            //getRooms();
            getPosts();
        });

        return binding.getRoot();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onLiked(int position, Posts postId, int likeStatus, int alreadyStatus) {
        likePost(position, postId, likeStatus, alreadyStatus);
    }

    private void likePost(final int position, final Posts post, int reaction, int alreadyStatus) {
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

                //updatePost(likeDislike.getData());
            }

            @Override
            public void onFail() {
                hideHud();
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
//
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    public void updatePost(Posts post, boolean isFlagged, boolean islike) {

        if (posts != null && posts.contains(post) && postsAdapter != null) {
            int index = posts.indexOf(post);

            postsAdapter.updateItem(index, post, isFlagged, islike);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setRoomAdapter() {

        roomCardAdapter = new RoomCardAdapter(getActivity(), rooms);
        roomLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRooms.setLayoutManager(roomLayoutManager);
        binding.rvRooms.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 23), Utils.dpToPx(getActivity(), 9.8f), 0));

 //binding.rvRooms.setAdapter(roomCardAdapter);
        binding.rvRooms.setVisibility(View.GONE);


        binding.rvRooms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = roomLayoutManager.getChildCount();
                mTotalItemCount = roomLayoutManager.getItemCount();
                mPastVisiblesItems = roomLayoutManager.findFirstVisibleItemPosition();
                if (currentRoomPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isRoomLoading)) {
                    currentRoomPage++;

                    isRoomLoading = true;
                    //getRooms();


                }
            }
        });


    }


    private void setPostAdapter() {

        postsAdapter = new PostsAdapter(getActivity(), posts, (HomeMainActivity) getActivity());
        postsAdapter.setFromRoom(false);
        postsAdapter.setIsfromHome(true);
        postsAdapter.setIsHome(isHome);
        postsAdapter.setHomeScreen(mScreen);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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


    }

    public void getRooms() {

        if (currentRoomPage == 1 && !isSwipeToRefresh)
            binding.loading.setVisibility(View.VISIBLE);

        new BaseCallback<GetRoomsResponse>((BaseActivity) getActivity(), mRepository.getApiService()
                .getHomeRooms(currentRoomPage, Constants.LIST_LENGTH, mScreen, isHome)) {
            @Override
            public void onSuccess(GetRoomsResponse response) {

                if (getActivity() == null)
                    return;

                if (!isPostLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;
                }


                isRoomLoading = false;

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (currentRoomPage == 1) {
                    rooms.clear();
                }

                rooms.addAll(response.getData());

                if (currentRoomPage == 1)
                    roomCardAdapter.notifyDataSetChanged();
                else
                    roomCardAdapter.notifyItemChanged(rooms.size() + 1, rooms);


                if (currentRoomPage == 1)
                    getPosts();

                isSwipeToRefresh = false;

            }

            @Override
            public void onFail() {
                isRoomLoading = false;
                isSwipeToRefresh = false;

                if (!isPostLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;
                }
                binding.swipeRefresh.setRefreshing(false);

            }
        };
    }


    public void getPosts() {

        binding.loading.setVisibility(View.VISIBLE);
        Observable<PostsResponse> observable;

        observable = mRepository.getApiService().getHomePosts(currentPostPage, Constants.LIST_LENGTH, mScreen, isHome);
        new BaseCallback<PostsResponse>(getBaseActivity(), observable) {
            @Override
            public void onSuccess(PostsResponse response) {
                isPostLoading = false;
                binding.loading.setVisibility(View.GONE);

                /*if (!isRoomLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;

                }*/
                binding.swipeRefresh.setRefreshing(false);
                //isPostLoading = false;
                if (currentPostPage == 1) {
                    posts.clear();
                }
                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                posts.addAll(response.getData());
                postsAdapter.setRoom(rooms);
                if (currentPostPage == 1) {
                    postsAdapter.notifyDataSetChanged();
                } else
                    postsAdapter.notifyItemChanged(posts.size() + 1, posts);


            }

            @Override
            public void onFail() {
                isPostLoading = false;
                binding.loading.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                /*if (!isRoomLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;

                }*/
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


            if (itemPosition == 0) {
                outRect.set(itemStartOffset, Utils.dpToPx(getActivity(), 16), itemEndOffset, itemBottomOffset);

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

}
