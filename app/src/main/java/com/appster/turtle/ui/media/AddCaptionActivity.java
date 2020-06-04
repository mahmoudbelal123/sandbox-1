package com.appster.turtle.ui.media;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomTagsAdapter;
import com.appster.turtle.adapter.UserTagAdapter;
import com.appster.turtle.databinding.ActivityAddCaptionBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
/**
 * A activity class for add caption
 */
public class AddCaptionActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddCaptionBinding mBinding;
    private RoomTagsAdapter mRoomTagsAdapter;
    private RetrofitRestRepository repository;
    private int mCurrentPage;
    private String query;
    private List<Tag> tags;
    private List<Tag> selectedTags;
    private int mTotalPagesAvailable;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private boolean isLoading;

    private LinearLayoutManager linearLayoutManager;
    private String caption = "";
    private boolean isUserTag;
    private BaseCallback<SearchUserResponse> baseCallback;
    private List<User> mUserTags;
    private UserTagAdapter mUserTagsAdapter;
    private ArrayList<Integer> mSelectedUserTags = new ArrayList<>();
    private int roomId;
    private boolean isPostEdit;
    private Posts posts;
    private ArrayList<User> mUserSelectedTags = new ArrayList<>();
    private boolean isFromRoom;

    @Override
    public String getActivityName() {
        return AddCaptionActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_caption);
        repository = ((ApplicationController) getApplicationContext()).provideRepository();
        selectedTags = new ArrayList<>();
        initUI();


    }

    private void initUI() {

        mBinding.tvAddTags.setText(R.string.add_caption_header);
        mBinding.tvNext.setText(R.string.save);
        mBinding.etTags.setMaxLines(4);
        mBinding.etTags.setHint(R.string.add_caption);

        mBinding.rvTags.addItemDecoration(new ItemDecorationView(this , 0 ,Utils.dpToPx(AddCaptionActivity.this, 12) , Utils.dpToPx(this , 5) , Utils.dpToPx(this , 5)));
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvTags.setLayoutManager(linearLayoutManager);

        mBinding.rvTags.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = linearLayoutManager.getChildCount();
                mTotalItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (tags != null || mUserTags != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getTags();
                    }
                }


            }
        });

        Bundle mBundle = getIntent().getExtras();

        if (mBundle.getParcelable(Constants.POST_QUERY) != null) {

            isFromRoom = mBundle.getBoolean(Constants.IS_FROM_ROOM);
            isPostEdit = true;
            posts = mBundle.getParcelable(Constants.POST_QUERY);
            selectedTags.clear();
            selectedTags = posts.getTags();

            mUserSelectedTags = posts.getTaggedUsers();

            mSelectedUserTags.clear();
            mSelectedUserTags = getTaggedUser(posts.getTaggedUsers());

            mBinding.tvAddTags.setText(R.string.edit_caption_header);
            mBinding.tvNext.setText(R.string.save);

            roomId = posts.getRooms().getId();
            caption = posts.getPostDetail().getPostMediaList().get(0).getCaption();

            mBinding.etTags.setText(caption);
        } else {
            isPostEdit = false;
            if (mBundle.getParcelableArrayList(Constants.TAGS) != null) {
                selectedTags.clear();
                selectedTags = mBundle.getParcelableArrayList(Constants.TAGS);

            }

            if (mBundle.getParcelableArrayList(Constants.USER_TAGS) != null) {
                mUserSelectedTags.clear();
                mUserSelectedTags = mBundle.getParcelableArrayList(Constants.USER_TAGS);

            }
            mSelectedUserTags.clear();
            mSelectedUserTags = getTaggedUser(mUserSelectedTags);

            roomId = mBundle.getInt(Constants.BundleKey.ROOM_ID, 0);

            if (mBundle.getString(Constants.CAPTION) != null) {
                caption = getIntent().getStringExtra(Constants.CAPTION);
                mBinding.etTags.setText(caption);
            }
        }

        //set on click listeners
        mBinding.ivBack.setOnClickListener(this);
        mBinding.tvNext.setOnClickListener(this);

        mBinding.etTags.setTagList(mUserSelectedTags);


        mBinding.etTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                int selection = mBinding.etTags.getSelectionEnd();

                String str = s.toString().substring(0, selection);
                if (str.lastIndexOf("#") > str.lastIndexOf(" ")) {
                    query = str.substring(str.lastIndexOf("#") + 1, str.length());
                    isUserTag = false;
                    mCurrentPage = 1;
                    if (query.length() > 0)
                        getTags();
                    else {

                        clearTagList();
                    }
                } else if (str.lastIndexOf("@") > str.lastIndexOf(" ")) {
                    query = str.substring(str.lastIndexOf("@") + 1, str.length());
                    isUserTag = true;
                    mCurrentPage = 1;
                    getTags();


                } else {
                    clearTagList();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //

            }
        });

    }

    private ArrayList<Integer> getTaggedUser(ArrayList<User> arrayList) {
        ArrayList<Integer> intUser = new ArrayList<>();
        for (User user : arrayList)
            intUser.add(user.getUserId());

        return intUser;
    }

    private void clearTagList() {
        if (tags != null) {
            tags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }

        if (mUserTags != null) {
            mUserTags.clear();
            if (mUserTagsAdapter != null)
                mUserTagsAdapter.notifyDataSetChanged();
        }

        if ((mUserTags == null || mUserTags.isEmpty()) && (tags == null || tags.isEmpty())) {
            mBinding.flList.setVisibility(View.INVISIBLE);

        } else
            mBinding.flList.setVisibility(View.VISIBLE);

    }


    private void getTags() {


        if (isUserTag) {

            Observable observable;
            if (query.isEmpty()) {
                observable = repository.getApiService()
                        .getMembers(roomId, mCurrentPage, Constants.LIST_LENGTH, query);
            } else {
                observable = repository.getApiService()
                        .searchUser(mCurrentPage, Constants.LIST_LENGTH, query);
            }

            if (baseCallback != null)
                baseCallback.cancel();

            baseCallback = new BaseCallback<SearchUserResponse>(AddCaptionActivity.this, observable) {
                @Override
                public void onSuccess(SearchUserResponse response) {


                    if (mCurrentPage == 1) {
                        mUserTags = response.getData();
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        isLoading = false;
                        mUserTagsAdapter = new UserTagAdapter(AddCaptionActivity.this, response.getData());
                        mBinding.rvTags.setAdapter(mUserTagsAdapter);

                    } else {
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        mUserTags.addAll(response.getData());
                        isLoading = false;
                        mUserTagsAdapter.notifyDataSetChanged();
                    }

                    User deleteUser = null;
                    for (User user : mUserTags) {
                        if (user.getUserId() == PreferenceUtil.getUser().getUserId()) {
                            deleteUser = user;
                        }
                    }

                    if (deleteUser != null) {
                        mUserTags.remove(deleteUser);
                        mUserTagsAdapter.notifyDataSetChanged();

                    }

                    if ((mUserTags == null || mUserTags.isEmpty()) && (tags == null || tags.isEmpty())) {
                        mBinding.flList.setVisibility(View.INVISIBLE);

                    } else
                        mBinding.flList.setVisibility(View.VISIBLE);


                    if (query.isEmpty())
                        clearTagList();

                }

                @Override
                public void onFail() {

                    if ((mUserTags == null || mUserTags.isEmpty()) && (tags == null || tags.isEmpty())) {
                        mBinding.flList.setVisibility(View.INVISIBLE);

                    } else
                        mBinding.flList.setVisibility(View.VISIBLE);

                }
            };

        } else {

            new BaseCallback<GetTagsResponse>(AddCaptionActivity.this, repository.getApiService()
                    .getTags(mCurrentPage, Constants.LIST_LENGTH, query)) {
                @Override
                public void onSuccess(GetTagsResponse response) {

                    if (mCurrentPage == 1) {
                        tags = response.getData();
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        isLoading = false;
                        mRoomTagsAdapter = new RoomTagsAdapter(AddCaptionActivity.this, response.getData());
                        mBinding.rvTags.setAdapter(mRoomTagsAdapter);

                    } else {
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        tags.addAll(response.getData());
                        isLoading = false;
                        mRoomTagsAdapter.notifyDataSetChanged();

                    }

                    if ((mUserTags == null || mUserTags.isEmpty()) && (tags == null || tags.isEmpty())) {
                        mBinding.flList.setVisibility(View.INVISIBLE);

                    } else
                        mBinding.flList.setVisibility(View.VISIBLE);

                    if (query.isEmpty())
                        clearTagList();
                }

                @Override
                public void onFail() {

                    if ((mUserTags == null || mUserTags.isEmpty()) && (tags == null || tags.isEmpty())) {
                        mBinding.flList.setVisibility(View.INVISIBLE);

                    } else
                        mBinding.flList.setVisibility(View.VISIBLE);

                }
            };
        }


    }

    public void appendTag(Tag tag) {

        int selection = mBinding.etTags.getSelectionEnd();
        String originalStr = mBinding.etTags.getText().toString();

        if (originalStr.length() > 0) {
            String str;

            str = originalStr.substring(0, selection - 1);
            String postFixStr = "";

            if (selection < originalStr.length() || originalStr.lastIndexOf("#") == 0)
                postFixStr = originalStr.substring(selection, originalStr.length());
            else
                postFixStr = originalStr.substring(selection - 1, originalStr.length() - 1);


            if (postFixStr.contains("#")) {
                postFixStr = postFixStr.substring(postFixStr.indexOf("#"), postFixStr.length());
            }


            int newSelection;

            if (str.isEmpty()) {
                str = "#" + tag.getValue() + " " + postFixStr;
                newSelection = ("#" + tag.getValue()).length();

            } else {
                newSelection = (str.substring(0, str.lastIndexOf("#")) + "#" + tag.getValue() + " ").length();
                str = str.substring(0, str.lastIndexOf("#")) + "#" + tag.getValue() + " " + postFixStr;

            }


            mBinding.etTags.setText(str);
            mBinding.etTags.setSelection(newSelection);

            selectedTags.add(tag);

            clearTagList();
        }
    }

    public void appendTag(User tag) {
        String originalString = mBinding.etTags.getText().toString().trim();

        String endString = originalString.substring(mBinding.etTags.getSelectionEnd(), originalString.length());

        String startString = originalString.substring(0, mBinding.etTags.getSelectionEnd());
        startString = startString.substring(0, startString.lastIndexOf("@") + 1);

        String finalString = startString + tag.getUserName().toLowerCase() + endString;
        if (!endString.isEmpty() && endString.charAt(0) != ' ') {
            finalString = startString + tag.getUserName().toLowerCase() + " " + endString;
        }

        mBinding.etTags.setText(finalString);
        mBinding.etTags.setSelection((startString + tag.getUserName().toLowerCase()).length());


        if (!mSelectedUserTags.contains(tag.getUserId()))
            mSelectedUserTags.add(tag.getUserId());

        if (!mUserSelectedTags.contains(tag))
            mUserSelectedTags.add(tag);

        mBinding.etTags.setTagList(mUserSelectedTags);


        clearTagList();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                hideKeyboard();
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.tv_next:
                hideKeyboard();
                if (isPostEdit)
                    postMedia();
                else
                    addTags();
                break;
            default:
                break;
        }
    }

    private void addTags() {

        if (mBinding.etTags.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(findViewById(android.R.id.content), getString(R.string.tag_error_empty));

            return;
        }

        caption = mBinding.etTags.getText().toString();


        ArrayList<Tag> finalTags = new ArrayList<>();


        for (String tag : mBinding.etTags.getHashtags()) {
            Tag addtag = new Tag();
            addtag.setId(0);
            addtag.setValue(tag);
            finalTags.add(addtag);

        }


        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(Constants.TAGS, finalTags);
        returnIntent.putExtra(Constants.CAPTION, caption);
        returnIntent.putParcelableArrayListExtra(Constants.USER_TAGS, mUserSelectedTags);

        setResult(RESULT_OK, returnIntent);
        finish();


    }


    public void postMedia() {

        try {
            ArrayList<Integer> roomIds = new ArrayList<>();

            HashMap<String, RequestBody> task = new HashMap();

            RequestBody body = null;
            String filePath;

            showProgressBar();


            roomIds.add(roomId);

            ArrayList<Tag> finalTags = new ArrayList<>();


            for (String tag : mBinding.etTags.getHashtags()) {
                Tag addtag = new Tag();
                addtag.setId(0);
                addtag.setValue(tag);
                finalTags.add(addtag);

            }

            for (int i = 0; i < finalTags.size(); i++) {

                task.put("tags[" + i + "].id", createPartFromString(finalTags.get(i).getId() + ""));
                task.put("tags[" + i + "].value", createPartFromString(finalTags.get(i).getValue()));
            }


            task.put("taggedUserIds", createPartFromString(TextUtils.join(",", mSelectedUserTags)));
            task.put("caption", createPartFromString(mBinding.etTags.getText().toString()));
            task.put("roomIds", createPartFromString(roomId + ""));
            task.put("postId", createPartFromString(posts.getPostId() + ""));
            task.put("visibility", createPartFromString("0"));
            task.put("mediaType", createPartFromString(posts.getPostType() + ""));
            task.put("mediaId", createPartFromString(posts.getPostDetail().getPostMediaList().get(0).getMediaId() + ""));


            rx.Observable<PostsResponse> observable = repository.getApiService().addImagePost(body, task);
            new BaseCallback<PostsResponse>(AddCaptionActivity.this, observable) {
                @Override
                public void onSuccess(final PostsResponse response) {

                    try {
                        if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                            hideProgressBar();
                            posts = response.getData().get(0);


                            Alert.createAlert(AddCaptionActivity.this, "", getString(R.string.media_edited_successfully)).setOnDismissListener(dialog -> {

                                hideProgressBar();
                                finishActivity();


                            }).show();

                        }

                    } catch (Exception e) {
                        LogUtils.printStackTrace(e);
                    }
                }

                @Override
                public void onFail() {

                    hideProgressBar();
                }
            };
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    public void finishActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);
        Intent i = new Intent();
        if (isFromRoom) {
            bundle.putParcelable(Constants.POST_QUERY, posts);

            i.putExtras(bundle);
            setResult(RESULT_OK, i);
            finish();

        } else {
            bundle.putBoolean(Constants.IS_EDIT, true);
            ExplicitIntent.getsInstance().navigateTo(this, HomeMainActivity.class, bundle);

        }
    }


    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }


}
