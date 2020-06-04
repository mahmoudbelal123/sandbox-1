package com.appster.turtle.ui.poll;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCreatePollBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.request.Poll;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.AddTagsActivity;
import com.appster.turtle.util.Utils;
import com.appster.turtle.viewmodel.PollViewModel;
import com.appster.turtle.widget.CustomEditText;

import java.util.ArrayList;
/*
* Activity for create poll
 */
public class CreatePollActivity extends BaseActivity implements TextWatcher {

    private Poll mPoll;
    private ActivityCreatePollBinding mPollBinding;
    private ArrayList<Tag> tagList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpViews();
    }

    private void initDataBinding() {
        mPollBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_poll);
        mPoll = new Poll();
        new PollViewModel(this, mPoll);
    }

    private void setUpViews() {
        setClickListeners();
        createOptEt(Constants.MIN_NO_ET);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPoll.getRoomIds().add(bundle.getInt(Constants.BundleKey.ROOM_ID));
        }
    }

    private void createOptEt(int noOfOpt) {
        if (mPollBinding.optEtContainer.getChildCount() == Constants.MAX_CHOICES)
            return;
        for (int index = 0; index < noOfOpt; index++) {
            CustomEditText editText = (CustomEditText) LayoutInflater.from(this).inflate(R.layout.item_et_poll, null, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, Utils.dpToPx(this, getResources().getDimension(R.dimen.margin_xSmall)), 0, 0);
            editText.setLayoutParams(lp);
            editText.addTextChangedListener(this);
            String hint = getString(R.string.txt_hnt_opt) + String.valueOf(mPollBinding.optEtContainer.getChildCount() + 1);
            editText.setHint(hint);
            mPollBinding.optEtContainer.addView(editText);
        }
    }

    private void setClickListeners() {
        mPollBinding.addChoice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_join_black, 0);
        mPollBinding.addChoice.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();
                if (event.getRawX() >= (mPollBinding.addChoice.getRight() - mPollBinding.addChoice.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().left) - getResources().getDimension(R.dimen.default_hPadding)) {
                    createOptEt(1);
                    return true;
                }
            }
            return false;
        });
        mPollBinding.pollQuestion.addTextChangedListener(this);
    }


    public void onAddTagClick(View view) {

        Intent i = new Intent(CreatePollActivity.this, AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, tagList);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_CODE.REQUEST_ADD_TAG) {
            if (resultCode == RESULT_OK) {

                tagList.clear();
                tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                displayTags();

            }
        }
    }

    private void displayTags() {

        if (tagList == null || tagList.isEmpty())
            return;

        StringBuilder tagStr = new StringBuilder();
        for (Tag tag : tagList) {
            if (tagStr.length() == 0)
                tagStr = new StringBuilder("#" + tag.getValue());
            else
                tagStr.append(" #").append(tag.getValue());

        }

        mPollBinding.tvTagDesc.setText(tagStr.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void afterTextChanged(Editable editable) {
//
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }
}
