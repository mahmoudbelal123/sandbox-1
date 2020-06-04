/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomTagsAdapter;
import com.appster.turtle.databinding.ActivityAddTagsBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.SpaceItemDecoration;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
* Activity for add tag
 */
public class AddTagsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddTagsBinding mBinding;
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
    private BaseCallback<GetTagsResponse> bascAllback;

    @Override
    public String getActivityName() {
        return AddTagsActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_tags);
        repository = ((ApplicationController) getApplicationContext()).provideRepository();
        selectedTags = new ArrayList<>();
        initUI();


    }

    private void initUI() {

        mBinding.etTags.setMaxLines(10);

        mBinding.rvTags.addItemDecoration(new SpaceItemDecoration(Utils.dpToPx(AddTagsActivity.this, 12), true, true));
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
                    if (tags != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getTags();

                    }
                }


            }
        });

        if (getIntent().hasExtra(Constants.TAGS)) {
            selectedTags.clear();
            selectedTags = getIntent().getParcelableArrayListExtra(Constants.TAGS);
            displayTags();

        }

        if (getIntent().getBooleanExtra(Constants.IS_FROM_ROOM, false)) {
            mBinding.etTags.setHint("Search tags");
            mBinding.label.setVisibility(View.VISIBLE);
        } else
            mBinding.label.setVisibility(View.INVISIBLE);

        //set on click listeners
        mBinding.ivBack.setOnClickListener(this);
        mBinding.tvNext.setOnClickListener(this);


        mBinding.etTags.requestFocus();
        mBinding.etTags.setFilters(new InputFilter[]{filterSpecialChar()});
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

                    mCurrentPage = 1;
                    if (query.length() > 0)
                        getTags();
                    else {

                        clearTagList();
                    }
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

    private void clearTagList() {
        if (tags != null) {
            tags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }

        if (tags == null || tags.isEmpty())
            mBinding.flList.setVisibility(View.INVISIBLE);
        else
            mBinding.flList.setVisibility(View.VISIBLE);
    }

    private void displayTags() {

        if (selectedTags == null || selectedTags.isEmpty())
            return;

        StringBuilder tagStr = new StringBuilder();
        for (Tag tag : selectedTags) {
            tagStr.append("#").append(tag.getValue()).append(" ");

        }

        mBinding.etTags.setText(tagStr.toString());
    }

    private void getTags() {


        if (bascAllback != null)
            bascAllback.cancel();

        bascAllback = new BaseCallback<GetTagsResponse>(AddTagsActivity.this, repository.getApiService()
                .getTags(mCurrentPage, Constants.LIST_LENGTH, query)) {
            @Override
            public void onSuccess(GetTagsResponse response) {

                if (mCurrentPage == 1) {
                    tags = response.getData();
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;
                    mRoomTagsAdapter = new RoomTagsAdapter(AddTagsActivity.this, response.getData());
                    mBinding.rvTags.setAdapter(mRoomTagsAdapter);

                } else {
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    tags.addAll(response.getData());
                    isLoading = false;
                    mRoomTagsAdapter.notifyDataSetChanged();
                }

                if (tags == null || tags.isEmpty())
                    mBinding.flList.setVisibility(View.INVISIBLE);
                else
                    mBinding.flList.setVisibility(View.VISIBLE);

                if (query.isEmpty())
                    clearTagList();


            }

            @Override
            public void onFail() {

                if (query.isEmpty())
                    clearTagList();

                if (tags == null || tags.isEmpty())
                    mBinding.flList.setVisibility(View.INVISIBLE);
                else
                    mBinding.flList.setVisibility(View.VISIBLE);
            }
        };


    }

    public void appendTag(Tag tag) {

        int selection = mBinding.etTags.getSelectionEnd();
        String originalStr = mBinding.etTags.getText().toString();

        if (originalStr.length() > 0) {
            String str = mBinding.etTags.getText().toString();

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

    private InputFilter filterSpecialChar() {

        return (source, start, end, dest, dstart, dend) -> {

            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(source);
            // boolean b = m.matches();
            boolean b = m.find();
            String s = source + "";
            String etText = mBinding.etTags.getText().toString();

            if (etText.length() > 0 && etText.charAt(etText.length() - 1) == '#' && (s.equals("#") || s.equals(" ")))
                return "";

            if (b && !s.contains("#") && !s.contains(" ")) {
                return "";
            } else

                return null;
        };
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
                addTags();
                break;
        }
    }

    private void addTags() {

        if (mBinding.etTags.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(findViewById(android.R.id.content), getString(R.string.tag_error_empty));

            return;
        }

        ArrayList<Tag> finalTags = new ArrayList<>();

        if (mBinding.etTags.getHashtags() != null) {
            for (String tag : mBinding.etTags.getHashtags()) {
                Tag addtag = new Tag();
                addtag.setId(0);
                addtag.setValue(tag);
                for (Tag selectedTag : selectedTags) {
                    if (selectedTag.getValue().equals(tag)) {
                        addtag.setId(selectedTag.getId());
                        break;
                    }
                }
                finalTags.add(addtag);


            }
        }

        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(Constants.TAGS, finalTags);
        setResult(RESULT_OK, returnIntent);
        finish();


    }


}
