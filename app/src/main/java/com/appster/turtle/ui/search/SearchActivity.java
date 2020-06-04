package com.appster.turtle.ui.search;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivitySearchBinding;
import com.appster.turtle.databinding.ItemSearchBinding;
import com.appster.turtle.model.SearchRoomsNewModel;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.AddRoomNewResponse;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.HomeTopRoomsResponse;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;

/**
 * Created by abhaykant on 15/02/18.
 * Activity for room search
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SearchActivity.class.getName();
    private ActivitySearchBinding mBinder;
    private BaseCallback<HomeTopRoomsResponse> mHomeBaseCallback;
    private RetrofitRestRepository mRepository;
    private final int NUMBER_OF_POPULAR_ROOMS = 3;
    private String query = "";

    @Override
    public String getActivityName() {
        return SearchActivity.class.getSimpleName();
    }

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_search);

        Bundle bundle = getIntent().getExtras();
        mBinder.ivClear.setVisibility(View.INVISIBLE);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getTopRooms(query, true);
    }

    private void init() {
        mRepository = ((ApplicationController) this.getApplicationContext()).provideRepository();

        mBinder.ivClear.setOnClickListener(this);
        mBinder.ivBack.setOnClickListener(this);


        mBinder.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 0)
                    mBinder.ivClear.setVisibility(View.INVISIBLE);
                else
                    mBinder.ivClear.setVisibility(View.VISIBLE);


                mHandler.postDelayed(() -> {
                    query = charSequence.toString();
                    getTopRooms(query, false);

                }, 200);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Bundle bun = new Bundle();
//        bun.putBoolean(Constants.BundleKey.ROOM_REFRESH, true);
//        ExplicitIntent.getsInstance().navigateForResult(SearchActivity.this, HomeMainActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bun);

    }

    private void getTopRooms(String query, boolean isProgessBar) {

        if (mHomeBaseCallback != null) {
            mHomeBaseCallback.cancel();
        }

        mHomeBaseCallback = new BaseCallback<HomeTopRoomsResponse>(this,
                mRepository.getApiService().getTopRooms(NUMBER_OF_POPULAR_ROOMS, query), isProgessBar) {

            @Override
            public void onSuccess(HomeTopRoomsResponse response) {
                hideProgressBar();
                if (response.getData() != null && response.getMeta().getCode() == 200) {

                    setRoomsAndPeopleData(response.getData(), query);
                }

            }


            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    private void setRoomsAndPeopleData(final HomeTopRoomsResponse.Data data, String query) {
        mBinder.layoutSearch.removeAllViews();

        LayoutInflater layoutInflater = getLayoutInflater();
        View viewCategorySpaces = layoutInflater.inflate(R.layout.item_search_home_tag, mBinder.layoutSearch, false);
        TextView tvCategorySpaces = viewCategorySpaces.findViewById(R.id.tv_tag);
        tvCategorySpaces.setText(R.string.popular_space);
        mBinder.layoutSearch.addView(viewCategorySpaces);

        if (!query.isEmpty())
            tvCategorySpaces.setText(R.string.cap_space);


        // for People
        View view;
        if (data.getHotRooms() != null && data.getHotRooms().size() > 0) {
            for (int i = 0; i < data.getHotRooms().size(); i++) {
                // Add the text layout to the parent layout
                view = layoutInflater.inflate(R.layout.item_search, mBinder.layoutSearch, false);
                // In order to get the view we have to use the new view with text_layout in it
                TextView textSpacesName = view.findViewById(R.id.tv_name);
                TextView textDetail = view.findViewById(R.id.tv_type);
                textSpacesName.setText(data.getHotRooms().get(i).getRoomName());
                textSpacesName.setVisibility(View.VISIBLE);
             //   LogUtils.LOGD("isGlobal", data.getHotRooms().get(i).isGlobalRoom() + " ");
                String type = data.getHotRooms().get(i).isGlobalRoom() ? "Global" : "Campus";
                textDetail.setText(type.concat("/").concat(data.getHotRooms().get(i).getMembersCount() + " members"));
                final ImageView ivUserStatus = view.findViewById(R.id.iv_add);
                final ImageView ivAccept = view.findViewById(R.id.iv_accept);
                final ImageView ivDecline = view.findViewById(R.id.iv_decline);
                final TextView tvResponse = view.findViewById(R.id.tv_response_label);

                ivUserStatus.setTag(i);
                ivAccept.setTag(i);
                ivDecline.setTag(i);
                textSpacesName.setTag(i);
                textDetail.setTag(i);


                ivUserStatus.setVisibility(View.VISIBLE);
                ivAccept.setVisibility(View.GONE);
                ivDecline.setVisibility(View.GONE);
                tvResponse.setVisibility(View.GONE);

                if (data.getHotRooms().get(i).getUserReqStatus() == Constants.UserAddedRoomStatus.PENDING) {

                    if (data.getHotRooms().get(i).isRequestedByMe()) {
                        ivUserStatus.setImageResource(R.drawable.ic_pending_req);
                    } else {
                        ivUserStatus.setVisibility(View.INVISIBLE);
                        ivAccept.setVisibility(View.VISIBLE);
                        ivDecline.setVisibility(View.VISIBLE);
                        tvResponse.setVisibility(View.VISIBLE);

                    }
                } else if (data.getHotRooms().get(i).getUserReqStatus() == Constants.UserAddedRoomStatus.ADDED) {
                    ivUserStatus.setImageResource(R.drawable.ic_accepted_req);

                } else {
                    ivUserStatus.setImageResource(R.drawable.ic_add_grey);

                }

                // Add the text view to the parent layout
                mBinder.layoutSearch.addView(view);

                if (i == data.getHotRooms().size() - 1) {
                    View viewSeeMore = layoutInflater.inflate(R.layout.item_see_more, mBinder.layoutSearch, false);
                    TextView tvSeeMore = viewSeeMore.findViewById(R.id.tv_see_more);

                    if (i == data.getHotRooms().size() - 1) {
                        tvSeeMore.setVisibility(View.VISIBLE);
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                    }
                    mBinder.layoutSearch.addView(viewSeeMore);

                    tvSeeMore.setOnClickListener(view1 -> {

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.POST_QUERY, query);
                        Intent intent = new Intent(SearchActivity.this, SearchSpacesMoreActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });

                }
                textSpacesName.setOnClickListener(view12 -> {
                    Bundle bundle = new Bundle();
                    int tagVal = (int) ivUserStatus.getTag();

                    bundle.putInt(Constants.BundleKey.ROOM_ID, data.getHotRooms().get(tagVal).getRoomId());
                    bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);
                    ExplicitIntent.getsInstance().navigateTo(SearchActivity.this, RoomActivityCoordinator.class, bundle);
                });

                textDetail.setOnClickListener(view13 -> {
                    Bundle bundle = new Bundle();
                    int tagVal = (int) ivUserStatus.getTag();

                    bundle.putInt(Constants.BundleKey.ROOM_ID, data.getHotRooms().get(tagVal).getRoomId());
                    bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

                    ExplicitIntent.getsInstance().navigateTo(SearchActivity.this, RoomActivityCoordinator.class, bundle);
                });


                ivUserStatus.setOnClickListener(view14 -> {

                    int tagVal = (int) ivUserStatus.getTag();
                 //   LogUtils.LOGD(TAG, "roomName:" + data.getHotRooms().get(tagVal).getRoomName());
                   // LogUtils.LOGD(TAG, "roomId:" + data.getHotRooms().get(tagVal).getRoomId());

                    if (data.getHotRooms().get(tagVal).getUserReqStatus() == Constants.UserAddedRoomStatus.NOT_INVITED ||
                            data.getHotRooms().get(tagVal).getUserReqStatus() == Constants.UserAddedRoomStatus.REJECTED) {
                        addRoom(data.getHotRooms().get(tagVal), ivUserStatus);
                    }
                });

                ivAccept.setOnClickListener(view15 -> {

                    int tagVal = (int) ivUserStatus.getTag();
                 //   LogUtils.LOGD(TAG, "roomName:" + data.getHotRooms().get(tagVal).getRoomName());
                  //  LogUtils.LOGD(TAG, "roomId:" + data.getHotRooms().get(tagVal).getRoomId());

                    new BaseCallback<JoinRoomResponse>(SearchActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().roomInvite(data.getHotRooms().get(tagVal).getRoomId(), true), true) {

                        @Override
                        public void onSuccess(JoinRoomResponse response) {

                            data.getHotRooms().get(tagVal).setUserReqStatus(response.getData().getUserReqStatus());
                            data.getHotRooms().get(tagVal).setRequestedByMe(true);
                            ivUserStatus.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.GONE);
                            tvResponse.setVisibility(View.GONE);
                            ivUserStatus.setImageResource(R.drawable.ic_accepted_req);


                        }

                        @Override
                        public void onFail() {

                        }
                    };
                });

                ivDecline.setOnClickListener(view16 -> {

                    int tagVal = (int) ivUserStatus.getTag();
                //    LogUtils.LOGD(TAG, "roomName:" + data.getHotRooms().get(tagVal).getRoomName());
                  //  LogUtils.LOGD(TAG, "roomId:" + data.getHotRooms().get(tagVal).getRoomId());

                    new BaseCallback<JoinRoomResponse>(SearchActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().roomInvite(data.getHotRooms().get(tagVal).getRoomId(), false), true) {

                        @Override
                        public void onSuccess(JoinRoomResponse response) {

                            data.getHotRooms().get(tagVal).setUserReqStatus(response.getData().getUserReqStatus());
                            data.getHotRooms().get(tagVal).setRequestedByMe(true);
                            ivUserStatus.setVisibility(View.VISIBLE);
                            ivAccept.setVisibility(View.GONE);
                            ivDecline.setVisibility(View.GONE);
                            tvResponse.setVisibility(View.GONE);

                            ivUserStatus.setImageResource(R.drawable.ic_add_grey);


                        }

                        @Override
                        public void onFail() {

                        }
                    };
                });
            }
        } else {
            //text for no result

            View viewNoResult = layoutInflater.inflate(R.layout.item_search_seperator_tag, mBinder.layoutSearch, false);
            TextView tvNoResult = viewNoResult.findViewById(R.id.tv_tag);
            tvNoResult.setText(getString(R.string.no_result_found));
            tvNoResult.setPadding(0, 50, 0, 50);

            Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                    getString(R.string.eina_01_regular));
            // set TypeFace to the TextView or Edittext
            tvNoResult.setTypeface(mtypeFace);
            tvNoResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            tvNoResult.setGravity(Gravity.START);
            mBinder.layoutSearch.addView(viewNoResult);
        }


        View viewCategory = layoutInflater.inflate(R.layout.item_search_home_tag, mBinder.layoutSearch, false);
        TextView tvCategory = viewCategory.findViewById(R.id.tv_tag);
        tvCategory.setText(R.string.popular_people);
        mBinder.layoutSearch.addView(viewCategory);

        if (!query.isEmpty())
            tvCategory.setText(R.string.cap_people);


        if (data.getPopularUsers() != null && data.getPopularUsers().size() > 0) {
            for (int i = 0; i < data.getPopularUsers().size(); i++) {
                // Add the text layout to the parent layout
                ItemSearchBinding itemSearchBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_search, mBinder.layoutSearch, false);

                view = itemSearchBinding.getRoot();
                itemSearchBinding.setUser(data.getPopularUsers().get(i));
                // In order to get the view we have to use the new view with text_layout in it
                TextView textSpacesName = view.findViewById(R.id.tv_name);
                view.findViewById(R.id.tv_type).setVisibility(View.GONE);

                String fullName = data.getPopularUsers().get(i).getFullName();
                String userName = "";

                if (data.getPopularUsers().get(i).getUserName() != null)
                    userName = data.getPopularUsers().get(i).getUserName().length() > 0 ? " @" + data.getPopularUsers().get(i).getUserName() : "@" + data.getPopularUsers().get(i).getUserName();
                String fullString = fullName + userName;

                SpannableStringBuilder SS = new SpannableStringBuilder(fullString);
                SS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.textcolor_room_name_40)), fullName.length(), fullString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SS.setSpan(new RelativeSizeSpan(0.7f), fullName.length(), fullString.length(), 0);
                textSpacesName.setText(SS);

                final ImageView ivUserStatus = view.findViewById(R.id.iv_add);
                final TextView tvResponse = view.findViewById(R.id.tv_response_label);

                final ImageView ivAccept = view.findViewById(R.id.iv_accept);
                final ImageView ivDecline = view.findViewById(R.id.iv_decline);
                ivUserStatus.setTag(i);
                ivAccept.setTag(i);
                ivDecline.setTag(i);

                itemSearchBinding.flImage.setVisibility(View.VISIBLE);

                ivUserStatus.setVisibility(View.VISIBLE);
                ivAccept.setVisibility(View.GONE);
                ivDecline.setVisibility(View.GONE);
                tvResponse.setVisibility(View.GONE);


                if (data.getPopularUsers().get(i).getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING) {

                    if (data.getPopularUsers().get(i).isRequestedByMe()) {
                        ivUserStatus.setImageResource(R.drawable.ic_pending_req);
                    } else {
                        ivUserStatus.setVisibility(View.INVISIBLE);
                        ivAccept.setVisibility(View.VISIBLE);
                        ivDecline.setVisibility(View.VISIBLE);
                        tvResponse.setVisibility(View.VISIBLE);

                    }

                } else if (data.getPopularUsers().get(i).getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                        data.getPopularUsers().get(i).getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED) {
                    ivUserStatus.setImageResource(R.drawable.ic_add_grey);

                } else {
                    ivUserStatus.setImageResource(R.drawable.ic_accepted_req);

                }


                mBinder.layoutSearch.addView(view);

                if (i == data.getPopularUsers().size() - 1) {

                    View viewSeeMore = layoutInflater.inflate(R.layout.item_see_more, mBinder.layoutSearch, false);
                    TextView tvSeeMore = viewSeeMore.findViewById(R.id.tv_see_more);
                    if (i == data.getPopularUsers().size() - 1) {
                        tvSeeMore.setVisibility(View.VISIBLE);
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                    }

                    mBinder.layoutSearch.addView(viewSeeMore);

                    tvSeeMore.setOnClickListener(view18 -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.POST_QUERY, query);
                        Intent intent = new Intent(SearchActivity.this, SearchPeopleMoreActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });

                }

                ivUserStatus.setOnClickListener(view17 -> {

                    int tagVal = (int) ivUserStatus.getTag();
                 //   LogUtils.LOGD(TAG, "userName:" + data.getPopularUsers().get(tagVal).getFullName());
                   // LogUtils.LOGD(TAG, "userId:" + data.getPopularUsers().get(tagVal).getUserId());


                    if (data.getPopularUsers().get(tagVal).getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                            data.getPopularUsers().get(tagVal).getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED) {
                        actionOnRequest(data.getPopularUsers().get(tagVal).getUserReqStatus(), data.getPopularUsers().get(tagVal).getUserId(), ivUserStatus, data.getPopularUsers().get(tagVal));
                    }
                });

                ivAccept.setOnClickListener(view19 -> {

                    int tagVal = (int) ivUserStatus.getTag();

                    BaseCallback<FriendRequestResponse> baseCallback = new BaseCallback<FriendRequestResponse>(SearchActivity.this, mRepository.getApiService().actionFriendRequest(data.getPopularUsers().get(tagVal).getUserId(), 2)) {
                        @Override
                        public void onSuccess(FriendRequestResponse response) {

                            if (response.getData() != null) {

                                data.getPopularUsers().get(tagVal).setUserReqStatus(response.getData().getUserReqStatus());
                                data.getPopularUsers().get(tagVal).setRequestedByMe(true);
                                ivUserStatus.setVisibility(View.VISIBLE);
                                ivAccept.setVisibility(View.GONE);
                                ivDecline.setVisibility(View.GONE);
                                tvResponse.setVisibility(View.GONE);
                                ivUserStatus.setImageResource(R.drawable.ic_accepted_req);


                            }


                        }

                        @Override
                        public void onFail() {

                        }
                    };
                });

                ivDecline.setOnClickListener(view110 -> {

                    int tagVal = (int) ivUserStatus.getTag();

                    BaseCallback<FriendRequestResponse> baseCallback = new BaseCallback<FriendRequestResponse>(SearchActivity.this, mRepository.getApiService().actionFriendRequest(data.getPopularUsers().get(tagVal).getUserId(), 3)) {
                        @Override
                        public void onSuccess(FriendRequestResponse response) {

                            if (response.getData() != null) {

                                data.getPopularUsers().get(tagVal).setUserReqStatus(response.getData().getUserReqStatus());
                                data.getPopularUsers().get(tagVal).setRequestedByMe(true);
                                ivUserStatus.setVisibility(View.VISIBLE);
                                ivAccept.setVisibility(View.INVISIBLE);
                                ivDecline.setVisibility(View.INVISIBLE);
                                tvResponse.setVisibility(View.GONE);

                                ivUserStatus.setImageResource(R.drawable.ic_add_grey);


                            }


                        }

                        @Override
                        public void onFail() {

                        }
                    };
                });
            }
        } else {

            View viewNoResult = layoutInflater.inflate(R.layout.item_search_seperator_tag, mBinder.layoutSearch, false);
            TextView tvNoResult = viewNoResult.findViewById(R.id.tv_tag);
            tvNoResult.setText(getString(R.string.no_result_found));
            tvNoResult.setPadding(0, 50, 0, 50);

            Typeface mtypeFace = Typeface.createFromAsset(getAssets(),
                    getString(R.string.eina_01_regular));
            // set TypeFace to the TextView or Edittext
            tvNoResult.setTypeface(mtypeFace);
            tvNoResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            tvNoResult.setGravity(Gravity.START);
            mBinder.layoutSearch.addView(viewNoResult);
        }
    }


    private void actionOnRequest(final int friendStatus, final int friendId, final ImageView view, final User user) {
        showProgressBar();
        int action = 0;

        /*
          q== 1) {//Send new Friend request
         q == 2) { // accept friend request
         q == 3) { //decline request

         */

        if (friendStatus == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED || friendStatus == Constants.MyFriend.STATUS_REJECTED) {
            action = 1;
        }

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        BaseCallback<FriendRequestResponse> baseCallback = new BaseCallback<FriendRequestResponse>(SearchActivity.this, mRepository.getApiService().actionFriendRequest(friendId, action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {
                hideProgressBar();
                if (response.getData().getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING) {
                    user.setUserReqStatus(Constants.MyFriend.FRIEND_STATUS_PENDING);
                    view.setImageResource(R.drawable.ic_pending_req);
                    user.setRequestedByMe(true);
                } else if (response.getData().getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED) {
                    view.setImageResource(R.drawable.ic_add_grey);
                }
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };
    }


    private void addRoom(final SearchRoomsNewModel roomsModel, final ImageView ivStatus) {
        showProgressBar();

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        BaseCallback<AddRoomNewResponse> baseCallback = new BaseCallback<AddRoomNewResponse>(SearchActivity.this, mRepository.getApiService().addRoom(roomsModel.getRoomId())) {
            @Override
            public void onSuccess(AddRoomNewResponse response) {
                hideProgressBar();
                if (response.getMeta().getCode() == 200) {
                    if (roomsModel.getRoomType() == Constants.ROOM_TYPE.PRIVATE) {
                        roomsModel.setUserReqStatus(Constants.UserAddedRoomStatus.PENDING);
                        ivStatus.setImageResource(R.drawable.ic_pending_req);
                        roomsModel.setRequestedByMe(true);
                    } else {
                        roomsModel.setUserReqStatus(Constants.UserAddedRoomStatus.ADDED);
                        ivStatus.setImageResource(R.drawable.ic_accepted_req);
                    }

                }
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };
    }


}



