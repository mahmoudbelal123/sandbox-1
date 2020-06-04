package com.appster.turtle.ui.textpost;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomTagsAdapter;
import com.appster.turtle.databinding.ActivityCreateTextPostBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.CreateTextPostRequest;
import com.appster.turtle.network.response.CreateTextPostResponse;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;
/*
* Activity for create text post
 */
public class CreateTextPostActivity extends BaseActivity implements TextWatcher, View.OnClickListener {

    private ActivityCreateTextPostBinding mBinding;
    private int mCharRemaining;

    private RetrofitRestRepository mRepository;

    private int mTotalPagesAvailable;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private boolean isLoading;

    private RoomTagsAdapter mRoomTagsAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String mQuery;
    private List<Tag> mTags;
    private List<Tag> mSelectedTags;

    private Room mRoom;
    private boolean mForEdit;
    private Posts mPost;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_text_post);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        mSelectedTags = new ArrayList<>();

        Intent i = getIntent();
        mRoom = i.getParcelableExtra(Constants.BundleKey.ROOM);

        initUI();

        if (i.hasExtra(Constants.BundleKey.FOR_EDIT)) {
            mForEdit = i.getBooleanExtra(Constants.BundleKey.FOR_EDIT, false);
            mPost = i.getParcelableExtra(Constants.BundleKey.MEETUP);
            setUpHeader(mBinding.clHeader.clHeader, getString(R.string.edit_post), getString(R.string.post));

            mBinding.etPostText.setText(mPost.getPostDetail().getText());
            mBinding.etPostText.setSelection(mPost.getPostDetail().getText().length());

        }
    }

    private void initUI() {
        setUpHeader(mBinding.clHeader.clHeader, getString(R.string.create_post), getString(R.string.post));

        mBinding.clHeader.tvHeaderEnd.setOnClickListener(this);
        mBinding.etPostText.addTextChangedListener(this);

        //enable scrolling in editText
        mBinding.etPostText.setMovementMethod(new ScrollingMovementMethod());
        mBinding.etPostText.setVerticalScrollBarEnabled(true);

        //set remaining characters text
        mCharRemaining = Constants.MAX_CHARACTERS_TEXT_POST;
        mBinding.tvRemainingCharacters.setText(String.valueOf(Constants.MAX_CHARACTERS_TEXT_POST));
        mBinding.tvRemainingCharacters.append(getString(R.string.characters_max));


        //for tags
        mBinding.rvHashtagSuggestions.addItemDecoration(new SpaceItemDecoration(12));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvHashtagSuggestions.setLayoutManager(mLinearLayoutManager);

        //load certain number of suggestions at a time, not all at once
        mBinding.rvHashtagSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLinearLayoutManager.getChildCount();
                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mPastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (mTags != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getTagSuggestions();

                    }
                }


            }
        });


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //calculate remaining characters
        mCharRemaining = Constants.MAX_CHARACTERS_TEXT_POST - charSequence.length();
        updateCharactersRemaining();

        checkForHashTags(charSequence);

        validateData();
    }

    private void validateData() {
        if (mBinding.etPostText.getText().toString().trim().length() > 0) {
            mBinding.clHeader.tvHeaderEnd.setEnabled(true);
        } else {
            mBinding.clHeader.tvHeaderEnd.setEnabled(false);
        }
    }

    private void updateCharactersRemaining() {

        //if no character is entered, reset remaining characters string
        if (mCharRemaining == Constants.MAX_CHARACTERS_TEXT_POST) {
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_max), Constants.MAX_CHARACTERS_TEXT_POST));
        } else if (mCharRemaining == 1) {
            // 'characters' replaced by 'character'
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.character_remaining_half_string), mCharRemaining));
        } else {
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_remaining_half_string), mCharRemaining));
        }

    }

    private void checkForHashTags(CharSequence s) {

        int selection = mBinding.etPostText.getText().length();

        String str = s.toString().substring(0, selection);
        if (str.lastIndexOf("#") > str.lastIndexOf(" ")) {
            mQuery = str.substring(str.lastIndexOf("#") + 1, str.length());

            mCurrentPage = 1;
            if (mQuery.length() > 0)
                getTagSuggestions();
            else {
                clearTagList();
            }
        } else {
            clearTagList();

        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
//
    }

    private void showRecyclerView() {
        mBinding.rvHashtagSuggestions.setVisibility(View.VISIBLE);
        mBinding.vTagHeader.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView() {
        mBinding.rvHashtagSuggestions.setVisibility(View.INVISIBLE);
        mBinding.vTagHeader.setVisibility(View.GONE);
    }

    private void getTagSuggestions() {

        showRecyclerView();

        new BaseCallback<GetTagsResponse>(CreateTextPostActivity.this, mRepository.getApiService()
                .getTags(mCurrentPage, Constants.LIST_LENGTH, mQuery)) {
            @Override
            public void onSuccess(GetTagsResponse response) {

                if (mCurrentPage == 1) {
                    mTags = response.getData();
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;
                    mRoomTagsAdapter = new RoomTagsAdapter(CreateTextPostActivity.this, response.getData());
                    mBinding.rvHashtagSuggestions.setAdapter(mRoomTagsAdapter);

                } else {
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    mTags.addAll(response.getData());
                    isLoading = false;
                    mRoomTagsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    private void clearTagList() {

        hideRecyclerView();

        if (mTags != null) {
            mTags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }
    }

    public void appendTag(Tag tag) {
        String originalString = mBinding.etPostText.getText().toString().trim();

        originalString = originalString.substring(0, originalString.lastIndexOf("#") + 1);

        mBinding.etPostText.setText(originalString + tag.getValue() + " ");
        mBinding.etPostText.setSelection(mBinding.etPostText.getText().length());

        if (!mSelectedTags.contains(tag))
            mSelectedTags.add(tag);

        clearTagList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                postClicked();
                break;
        }
    }

    private void postClicked() {

        showProgressBar();

        List<Integer> roomIds = new ArrayList<>();
        CreateTextPostRequest textPostRequest = new CreateTextPostRequest();
        if (mForEdit) {

            roomIds.add(mPost.getRooms().getId());
            textPostRequest.setPostId(mPost.getPostId());
        } else {
            roomIds.add(mRoom.getRoomId());
            textPostRequest.setPostId(0); //0 for new post, 1 for edit post

        }
        textPostRequest.setText(mBinding.etPostText.getText().toString().trim());
        textPostRequest.setTags(getHashTags());
        textPostRequest.setRoomIds(roomIds);
        textPostRequest.setVisibility(0);

        new BaseCallback<CreateTextPostResponse>(CreateTextPostActivity.this, mRepository.getApiService().createTextPost(textPostRequest)) {

            @Override
            public void onSuccess(CreateTextPostResponse response) {
                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    String message = mForEdit ? getString(R.string.post_edit_message) : getString(R.string.post_created_message);

                    Alert.createAlert(CreateTextPostActivity.this, "", message).setOnDismissListener(dialog -> {

                        hideProgressBar();
                        finish();

                    }).show();
                }
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    private List<Tag> getHashTags() {
        String[] tags = mBinding.etPostText.getText().toString().split("(?=#)");

        if (mBinding.etPostText.getText().toString().isEmpty())
            return null;

        if (tags.length == 0)
            return null;

        ArrayList<String> tagStrs = new ArrayList<>();
        for (String tag1 : tags) {
            if (tag1.trim().startsWith("#")) {
                String trimmedTag = tag1.trim().replaceFirst("#", "");
                if (trimmedTag.contains(" ")) {
                    trimmedTag = trimmedTag.split("\\s+")[0];
                }
                tagStrs.add(trimmedTag);
            }

        }


        ArrayList<Tag> finalTags = new ArrayList<>();

        for (String tagStr : tagStrs) {

            boolean added = false;

            for (Tag tag : mSelectedTags) {

                if (tag.getValue().equals(tagStr)) {
                    added = true;
                    finalTags.add(tag);
                    break;
                }
            }

            if (!added) {

                Tag tag = new Tag();
                tag.setId(0);
                tag.setValue(tagStr);

                finalTags.add(tag);
            }
        }

        return finalTags;

    }

}
