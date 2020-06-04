package com.appster.turtle.ui.post;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCreatePollBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.Poll;
import com.appster.turtle.network.request.PollAnswer;
import com.appster.turtle.network.response.CreateMeetupResponse;
import com.appster.turtle.network.response.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.AddTagsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.CustomEditText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Created  on 05/03/18 .
 * create poll fragment
 */

public class CreatePollPostFragment extends BaseFragment implements View.OnClickListener {
    private ActivityCreatePollBinding databinding;
    private Room mRoom;

    public Poll getmPoll() {
        return mPoll;
    }

    private Poll mPoll = new Poll();
    private RetrofitRestRepository repository;
    private ArrayList<Tag> tagList = new ArrayList<>();
    private ArrayList<Integer> roomId = new ArrayList<>();
    private ArrayList<PollAnswer> answers = new ArrayList<>();
    private LinkedHashMap<Integer, String> map = new LinkedHashMap<>();

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public CreatePollPostFragment() {
//
    }

    public static CreatePollPostFragment newInstance() {
        return new CreatePollPostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        databinding = DataBindingUtil.inflate(inflater, R.layout.activity_create_poll, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRoom = bundle.getParcelable(Constants.BundleKey.ROOM);
        }


        StringUtils.greyTextPart(databinding.tvText, ("Posting to " + mRoom.getValue()).toUpperCase(), mRoom.getValue().toUpperCase());
        init();


        return databinding.getRoot();

    }


    private void init() {

        roomId = new ArrayList<>();
        roomId.add(mRoom.getId());
        mPoll.setRoomIds(roomId);


        for (int i = 0; i < 2; i++) {
            createOpt(true);
        }
        repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();

        setClickListeners();
    }

    public void createOpt(boolean isDefault) {
        if (databinding.optEtContainer.getChildCount() == Constants.MAX_CHOICES) {
            showSnackBar(getString(R.string.cant_make_10_choices));
            return;
        }

        int getCount = databinding.optEtContainer.getChildCount();

        final CustomEditText editText = (CustomEditText) LayoutInflater.from(getActivity()).inflate(R.layout.item_et_poll, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, Utils.dpToPx(getActivity(), getResources().getDimension(R.dimen.margin_xSmall)), 0, 0);
        editText.setLayoutParams(lp);
        String hint = getString(R.string.txt_hnt_opt) + String.valueOf(databinding.optEtContainer.getChildCount() + 1);
        editText.setHint(hint);
        editText.setFocusable(true);

        editText.setTag(getCount);
        map.put(getCount, "");

        editText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (((Integer) v.getTag()) == databinding.optEtContainer.getChildCount() - 1) {
                    hideKeyboard();
                    clearFocus();
                }
                return false;
            }
            return true;
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                map.put((Integer) editText.getTag(), s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
//

            }
        });
        databinding.optEtContainer.addView(editText);
        if (!isDefault)
            editText.requestFocus();


    }

    private void clearFocus() {
        if (databinding.optEtContainer.getChildCount() > 0) {
            View view = databinding.optEtContainer.getFocusedChild();
            if (view != null)
                view.clearFocus();
        }
    }


    public void convertHashMapIntList() {
        if (answers != null) {
            answers.clear();
        }
        Set<Integer> keys = map.keySet();
        int index = 0;
        for (int key : keys) {

            PollAnswer pollAnswer = new PollAnswer();
            String ans = map.get(key);

            if (ans != null && !ans.isEmpty()) {
                pollAnswer.setAnswer(ans);
                pollAnswer.setAnswerOrder(index++);
                pollAnswer.setAnswerId(0);
                if (answers != null)
                    answers.add(pollAnswer);
            }
        }

        if (index < 2) {
            getBaseActivity().hideProgressBar();
            showSnackBar(getString(R.string.atleast_2_choices));

            return;

        }

        mPoll.setPollAnswersList(answers);

        new BaseCallback<CreateMeetupResponse>((BaseActivity) getActivity(), repository.getApiService()
                .postPoll(mPoll)) {
            @Override
            public void onSuccess(final CreateMeetupResponse pollResponse) {
                if (pollResponse.getMeta().getCode() == 200) {
                    Alert.createAlert(getActivity(), "", getActivity().getString(R.string.poll_created_message)).setOnDismissListener(dialog -> {
                        hideHud();
                        ((CreatePostActivity) getActivity()).finishActivity(pollResponse.getData().get(0));

                    }).show();
                } else {
                    ((BaseActivity) getActivity()).showSnackBar(pollResponse.getMeta().getMessage());
                }
            }

            @Override
            public void onFail() {
                hideHud();
            }
        };

    }


    private void setClickListeners() {
        databinding.tvTags.setOnClickListener(this);
        databinding.ivCloseBottom.setOnClickListener(this);
        databinding.addChoice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_join_black, 0);

        databinding.addChoice.setOnClickListener(v -> {

            createOpt(false);
        });
        databinding.pollQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPoll.setQuestion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//
            }
        });


    }


    public void onAddTagClick() {

        Intent i = new Intent(getActivity(), AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, tagList);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE.REQUEST_ADD_TAG) {
            if (resultCode == RESULT_OK) {

                tagList.clear();
                tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                mPoll.setTags(tagList);
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

        databinding.tvTagDesc.setText(tagStr.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tags:
                onAddTagClick();
                break;

            case R.id.iv_close_bottom:
                getActivity().finish();
                break;

            default:
                break;
        }
    }

    public boolean isVaildate() {
        if (mPoll == null) {
            return false;
        } else if (mPoll.getQuestion() == null || mPoll.getQuestion().isEmpty()) {
            showSnackBar(getString(R.string.question_cant_empy));
            return false;
        } else if (map == null || map.size() < 2) {
            showSnackBar(getString(R.string.error_choice));
            return false;
        }
        return true;
    }

    public void postPoll() {
        showProgressBar();
        convertHashMapIntList();

    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI(databinding.main);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard();
                clearFocus();
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
