package com.appster.turtle.ui.post;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomTagsAdapter;
import com.appster.turtle.adapter.UserTagAdapter;
import com.appster.turtle.databinding.FragmentCreateTextPostBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.CreateTextPostRequest;
import com.appster.turtle.network.response.CreateTextPostResponse;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.Room;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.SpaceItemDecoration;
import com.appster.turtle.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

/**
 * Created  on 05/03/18 .
 * activity for text post create
 */

public class CreateTextPostFragment extends BaseFragment implements TextWatcher, View.OnClickListener {
    private FragmentCreateTextPostBinding databinding;
    private String text;
    private RetrofitRestRepository mRepository;

    private int mTotalPagesAvailable;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private boolean isLoading;

    private RoomTagsAdapter mRoomTagsAdapter;
    private UserTagAdapter mUserTagsAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String mQuery;
    private List<Tag> mTags;
    private List<Tag> mSelectedTags;

    private Room mRoom;
    private boolean mForEdit;
    private Posts mPost;
    private boolean isUserTag;
    private BaseCallback<SearchUserResponse> baseCallback;
    private List<User> mUserTags;
    private List<Integer> mSelectedUserTags = new ArrayList<>();
    private ArrayList<User> mUserSelectedTags = new ArrayList<>();

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public CreateTextPostFragment() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public static CreateTextPostFragment newInstance() {
        CreateTextPostFragment fragment = new CreateTextPostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_text_post, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {


            mPost = bundle.getParcelable(Constants.BundleKey.POST);


            mForEdit = bundle.getBoolean(Constants.IS_EDIT, false);
            if (mForEdit) {
                mRoom = mPost.getRooms();
                text = mPost.getPostDetail().getText();
                databinding.etDescription.setText(text);
                mUserSelectedTags = mPost.getTaggedUsers();
                mSelectedUserTags = getTaggedUser(mPost.getTaggedUsers());
                databinding.etDescription.setTagList(mUserSelectedTags);

            } else {
                mRoom = bundle.getParcelable(Constants.BundleKey.ROOM);
                text = "";
            }


        }


        databinding.etDescription.setMentionEnabled(true);
        databinding.etDescription.setHashtagEnabled(true);


        StringUtils.greyTextPart(databinding.tvText, ("Posting to " + mRoom.getValue()).toUpperCase(), mRoom.getValue().toUpperCase());
        databinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (2000 - databinding.etDescription.getText().toString().length())));
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        mSelectedTags = new ArrayList<>();

        databinding.etDescription.setFilters((new InputFilter[]{new InputFilter.LengthFilter(2000)}));
        databinding.etDescription.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mspanable = databinding.etDescription.getText();
        initUI();
        if (mForEdit) {
            if (text != null && !text.isEmpty()) {
                List<String> tagList = setTagForEdit(text);
                for (int i = 0; i < tagList.size(); i++) {
                    LogUtils.LOGD("TAG1", tagList.get(i));
                    int start = text.indexOf(tagList.get(i));
                    tagCheck(tagList.get(i), start, start + tagList.get(i).length());
                }
            }
        }
        return databinding.getRoot();
    }

    private ArrayList<Integer> getTaggedUser(ArrayList<User> arrayList) {
        ArrayList<Integer> intUser = new ArrayList<>();
        for (User user : arrayList)
            intUser.add(user.getUserId());

        return intUser;
    }

    private List<String> setTagForEdit(String input) {
        int i = 0;
        Pattern patt = Pattern.compile("(#\\w+)\\b");
        Matcher match = patt.matcher(input);
        List<String> matStr = new ArrayList<>();

        while (match.find()) {

            matStr.add(match.group(1));
        }

        return matStr;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        checkForHashTags(charSequence);


    }

    Spannable mspanable;
    int hashTagIsComing = 0;


    private void tagCheck(String s, int start, int end) {

    }


    @Override
    public void afterTextChanged(Editable s) {
        databinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (2000 - databinding.etDescription.getText().toString().length())));
    }


    private void initUI() {


        databinding.etDescription.addTextChangedListener(this);

        //enable scrolling in editText
        databinding.etDescription.setMovementMethod(new ScrollingMovementMethod());
        databinding.etDescription.setVerticalScrollBarEnabled(true);

        //set remaining characters text


        //for tags
        databinding.rvHashtagSuggestions.addItemDecoration(new SpaceItemDecoration(11));
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        databinding.rvHashtagSuggestions.setLayoutManager(mLinearLayoutManager);

        //load certain number of suggestions at a time, not all at once
        databinding.rvHashtagSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLinearLayoutManager.getChildCount();
                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mPastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (mTags != null || mUserTags != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getTagSuggestions();

                    }
                }


            }
        });

        databinding.ivCloseBottom.setOnClickListener(this);

    }

    private void checkForHashTags(CharSequence s) {

        int selection = s.toString().length();

        String str = s.toString().substring(0, selection);
        if (str.lastIndexOf("#") > str.lastIndexOf(" ")) {
            isUserTag = false;
            mQuery = str.substring(str.lastIndexOf("#") + 1, str.length());

            mCurrentPage = 1;
            if (mQuery.length() > 0)
                getTagSuggestions();
            else {
                clearTagList();
            }
        } else if (str.lastIndexOf("@") > str.lastIndexOf(" ")) {
            mQuery = str.substring(str.lastIndexOf("@") + 1, str.length());
            isUserTag = true;
            mCurrentPage = 1;
            getTagSuggestions();


        } else

        {
            clearTagList();

        }


    }

    private void showRecyclerView() {
        databinding.rvHashtagSuggestions.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView() {
        databinding.rvHashtagSuggestions.setVisibility(View.GONE);

    }

    private void getTagSuggestions() {

        showRecyclerView();

        if (isUserTag) {

            Observable observable;
            if (mQuery.isEmpty()) {
                observable = mRepository.getApiService()
                        .getMembers(mRoom.getId(), mCurrentPage, Constants.LIST_LENGTH, mQuery);
            } else {
                observable = mRepository.getApiService()
                        .searchUser(mCurrentPage, Constants.LIST_LENGTH, mQuery);
            }

            if (baseCallback != null)
                baseCallback.cancel();

            baseCallback = new BaseCallback<SearchUserResponse>(getBaseActivity(), observable) {
                @Override
                public void onSuccess(SearchUserResponse response) {

                    if (mCurrentPage == 1) {
                        mUserTags = response.getData();
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        isLoading = false;
                        mUserTagsAdapter = new UserTagAdapter(CreateTextPostFragment.this, response.getData());
                        databinding.rvHashtagSuggestions.setAdapter(mUserTagsAdapter);

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
                }

                @Override
                public void onFail() {

                }
            };

        } else {

            new BaseCallback<GetTagsResponse>((BaseActivity) getActivity(), mRepository.getApiService()
                    .getTags(mCurrentPage, Constants.LIST_LENGTH, mQuery)) {
                @Override
                public void onSuccess(GetTagsResponse response) {

                    if (mCurrentPage == 1) {
                        mTags = response.getData();
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        isLoading = false;
                        mRoomTagsAdapter = new RoomTagsAdapter(getActivity(), response.getData());
                        databinding.rvHashtagSuggestions.setAdapter(mRoomTagsAdapter);

                    } else {
                        mTotalPagesAvailable = response.getPagination().getTotalPages();
                        mTags.addAll(response.getData());
                        isLoading = false;
                        mRoomTagsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFail() {

                }
            };
        }

    }

    private void clearTagList() {

        hideRecyclerView();

        if (mTags != null) {
            mTags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }

        if (mUserTags != null) {
            mUserTags.clear();
            if (mUserTagsAdapter != null)
                mUserTagsAdapter.notifyDataSetChanged();
        }
    }

    public void appendTag(Tag tag) {

        String originalString = databinding.etDescription.getText().toString().trim();

        String endString = originalString.substring(databinding.etDescription.getSelectionEnd(), originalString.length());

        String startString = originalString.substring(0, databinding.etDescription.getSelectionEnd());
        startString = startString.substring(0, startString.lastIndexOf("#") + 1);

        String finalString = startString + tag.getValue().toLowerCase() + endString;
        if (!endString.isEmpty() && endString.charAt(0) != ' ') {
            finalString = startString + tag.getValue().toLowerCase() + " " + endString;
        }


        databinding.etDescription.setText(finalString);
        databinding.etDescription.setSelection((startString + tag.getValue().toLowerCase()).length());

        for (Tag tag1 : mSelectedTags) {
            if (tag1.getId() != tag.getId()) {
                mSelectedTags.add(tag);
            }
        }


        clearTagList();
    }

    public void appendTag(User tag) {
        String originalString = databinding.etDescription.getText().toString().trim();

        String endString = originalString.substring(databinding.etDescription.getSelectionEnd(), originalString.length());

        String startString = originalString.substring(0, databinding.etDescription.getSelectionEnd());
        startString = startString.substring(0, startString.lastIndexOf("@") + 1);

        String finalString = startString + tag.getUserName().toLowerCase() + endString;
        if (!endString.isEmpty() && endString.charAt(0) != ' ') {
            finalString = startString + tag.getUserName().toLowerCase() + " " + endString;
        }


//                originalString=originalString.substring(0, mBinding.etComment.getSelectionEnd());

//        originalString = originalString.substring(originalString.lastIndexOf("@") + 1, originalString.length());

//        String finalString = tag.getUserName().toLowerCase().replace(originalString.toLowerCase(), "");


//        databinding.etDescription.setText(finalString);

        databinding.etDescription.setText(finalString);
        databinding.etDescription.setSelection((startString + tag.getUserName().toLowerCase()).length());

//        databinding.etDescription.setMovementMethod(LinkMovementMethod.getInstance());
//        databinding.etDescription.setSelection(databinding.etDescription.getText().length());

        if (!mSelectedUserTags.contains(tag.getUserId()))
            mSelectedUserTags.add(tag.getUserId());

        if (!mUserSelectedTags.contains(tag))
            mUserSelectedTags.add(tag);

        databinding.etDescription.setTagList(mUserSelectedTags);


        clearTagList();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_bottom:
                getActivity().finish();
                // ((CreatePostActivity)getActivity()).finishActivity();
                break;
        }
    }

    public void postClicked() {

        showProgressBar();

        List<Integer> roomIds = new ArrayList<>();
        CreateTextPostRequest textPostRequest = new CreateTextPostRequest();
        if (mForEdit) {

            roomIds.add(mPost.getRooms().getId());
            textPostRequest.setPostId(mPost.getPostId());
        } else {
            roomIds.add(mRoom.getId());
            textPostRequest.setPostId(0); //0 for new post, 1 for edit post

        }
        textPostRequest.setText(databinding.etDescription.getText().toString().trim());
        textPostRequest.setTags(getHashTags());
        textPostRequest.setTaggedUserIds(mSelectedUserTags);
        textPostRequest.setRoomIds(roomIds);
        textPostRequest.setVisibility(0);

        new BaseCallback<CreateTextPostResponse>((BaseActivity) getActivity(), mRepository.getApiService().createTextPost(textPostRequest)) {

            @Override
            public void onSuccess(final CreateTextPostResponse response) {
                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    String message = mForEdit ? getString(R.string.post_edit_message) : getString(R.string.post_created_message);

                    Alert.createAlert(getActivity(), "", message).setOnDismissListener(dialog -> {

                        hideHud();
                        //  getActivity().finish();
                        ((CreatePostActivity) getActivity()).finishActivity(response.getData().get(0));

                    }).show();
                }
            }

            @Override
            public void onFail() {

            }
        };

    }

    private List<Tag> getHashTags() {


        ArrayList<Tag> finalTag = new ArrayList<>();

        if (databinding.etDescription.getHashtags() != null) {
            for (String tag : databinding.etDescription.getHashtags()) {
                Tag addtag = new Tag();
                addtag.setId(0);
                addtag.setValue(tag);
                finalTag.add(addtag);
            }
        }

        return finalTag;
    }

    public boolean isValidate() {
        if (databinding.etDescription.getText().toString().isEmpty()) {
            showSnackBar(getString(R.string.empty_des));
            return false;
        }
        return true;
    }


}
