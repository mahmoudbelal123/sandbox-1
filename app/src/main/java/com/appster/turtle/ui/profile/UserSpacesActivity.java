package com.appster.turtle.ui.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.UserRoomAdapter;
import com.appster.turtle.databinding.ActivityUserSpacesBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for User space
 */
public class UserSpacesActivity extends BaseActivity {

    private ActivityUserSpacesBinding binding;
    private RetrofitRestRepository mRepository;
    private int userId;
    private UserRoomAdapter adapter;
    private ArrayList<Room> rooms = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int mTotalPostPagesAvailable;
    private boolean isPostLoading;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private int currentPostPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_spaces);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        userId = getIntent().getIntExtra(Constants.USER_ID, PreferenceUtil.getUser().getUserId());

        setUpHeader(false, binding.clHeader.clHeader, "MY SPACES", "", R.drawable.back_arrow);
        setAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPostPage = 1;

        getRooms();

    }

    private void setAdapter() {

        adapter = new UserRoomAdapter(rooms);
        linearLayoutManager = new LinearLayoutManager(UserSpacesActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvPost.setLayoutManager(linearLayoutManager);
        binding.rvPost.addItemDecoration(new ItemDecorationView(UserSpacesActivity.this, Utils.dpToPx(UserSpacesActivity.this, 6.7f), Utils.dpToPx(UserSpacesActivity.this, 6.7f), Utils.dpToPx(UserSpacesActivity.this, 15.4f), Utils.dpToPx(UserSpacesActivity.this, 15.4f)));

        binding.rvPost.setAdapter(adapter);

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
                    getRooms();


                }
            }
        });


    }

    private void getRooms() {

        Observable<GetRoomsResponse> observable;

        observable = mRepository.getApiService().getUserRooms(userId, currentPostPage, Constants.LIST_LENGTH);
        new BaseCallback<GetRoomsResponse>(UserSpacesActivity.this, observable) {
            @Override
            public void onSuccess(GetRoomsResponse response) {


                isPostLoading = false;

                if (currentPostPage == 1) {
                    rooms.clear();
                    adapter.notifyDataSetChanged();
                }


                mTotalPostPagesAvailable = response.getPagination().getTotalPages();
                rooms.addAll(response.getData());
                adapter.notifyItemChanged(rooms.size() + 1, rooms);

                binding.tvNo.setVisibility(View.GONE);

                if (rooms.isEmpty()) {
                    binding.tvNo.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFail() {
                isPostLoading = false;
                binding.tvNo.setVisibility(View.GONE);

                if (rooms.isEmpty()) {
                    binding.tvNo.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    public void setScrollPos(int scrollPos) {
        int scrollPos1 = scrollPos;
    }

    @Override
    public String getActivityName() {
        return UserSpacesActivity.class.getName();
    }
}
