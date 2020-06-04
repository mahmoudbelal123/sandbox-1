/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCreateNotesBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.widget.CustomEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
* Activity for create/edit note
 */
@SuppressWarnings("ALL")
public class CreateNotesActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ActivityCreateNotesBinding mBinding;
    private NotesModel mNotes;
    private boolean isEdit = false;
    private int pos;
    private RetrofitRestRepository mRepository;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_notes);

        initUI();
    }

    private void initUI() {
        setUpHeader(true, mBinding.header.clHeader, getString(R.string.create_notes), getString(R.string.next));
        mBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
        mNotes = new NotesModel();
        //set click listeners
        if (getIntent().getExtras() != null) {
            setExistingTitleClassPrice((NotesModel) getIntent().getExtras().getParcelable(Constants.NOTES));
            pos = getIntent().getIntExtra(Constants.POS, 0);

        } else {
            isEdit = false;
        }
        mBinding.header.tvHeaderEnd.setOnClickListener(this);
        //   set text watchers
        mBinding.etNotesTitle.addTextChangedListener(this);
        mBinding.etNotesClass.addTextChangedListener(this);
        mBinding.etNotesPrice.addTextChangedListener(this);

        mBinding.etNotesClass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!mBinding.etNotesClass.getText().toString().isEmpty()) {
                    mBinding.etNotesClass.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.white_selector_bg_radius_48));

                } else {
                    mBinding.etNotesClass.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.create_notes_selector));
                }
            }
            checkIfVaild();

        });


        mBinding.etNotesTitle.setOnFocusChangeListener((v, hasFoucs) ->
                {
                    if (!hasFoucs) {
                        if (!mBinding.etNotesTitle.getText().toString().isEmpty()) {
                            mBinding.etNotesTitle.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.white_selector_bg_radius_48));

                        } else {
                            mBinding.etNotesTitle.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.create_notes_selector));
                        }
                    }
                    checkIfVaild();
                }
        );

        mBinding.etNotesPrice.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                if (!mBinding.etNotesPrice.getText().toString().isEmpty()) {
                    mBinding.etNotesPrice.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.white_selector_bg_radius_48));

                } else {
                    mBinding.etNotesPrice.setBackgroundDrawable(ContextCompat.getDrawable(CreateNotesActivity.this, R.drawable.create_notes_selector));
                }
            }
            checkIfVaild();


        });
        mBinding.etNotesPrice.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(6),
                new DecimalDigitsInputFilter(3, 2),
        });
        mBinding.etNotesPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mBinding.etNotesPrice.clearFocus();
                    hideKeyboard();
                }
                return false;
            }
        });
        mBinding.etNotesPrice.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                //  nextClicked();
                if (nextClicked()) {
                    createNotes();
                }
                break;
        }
    }

    private boolean nextClicked() {

        if (StringUtils.isNullOrEmpty(mBinding.etNotesTitle.getText().toString().trim())) {
            StringUtils.displaySnackBar(mBinding.clParent, getString(R.string.invaild_note_title));
            return false;
        }

        if (StringUtils.isNullOrEmpty(mBinding.etNotesClass.getText().toString().trim())) {
            StringUtils.displaySnackBar(mBinding.clParent, getString(R.string.invaild_notes_class));
            return false;
        }

        if (StringUtils.isNullOrEmpty(mBinding.etNotesPrice.getText().toString().trim())) {
            StringUtils.displaySnackBar(mBinding.clParent, getString(R.string.invaild_price));
            return false;
        }

        if (Double.parseDouble(mBinding.etNotesPrice.getText().toString().trim()) > Constants.NOTES_PRICE_LIMIT) {
            showToast(getString(R.string.note_price_limit) + Constants.NOTES_PRICE_LIMIT);
            return false;
        }

        return true;
    }

    private void createNotes() {
        NotesModel addEditNoteRequest = new NotesModel();
        if (isEdit) {
            addEditNoteRequest.setId(mNotes.getId());
        } else {
            addEditNoteRequest.setId(0);
        }
        addEditNoteRequest.setTitle(mBinding.etNotesTitle.getText().toString().trim());
        addEditNoteRequest.setClassName(mBinding.etNotesClass.getText().toString().trim());
        addEditNoteRequest.setPrice(Double.parseDouble(mBinding.etNotesPrice.getText().toString().trim()));
        if (isEdit) {
            Bundle bundle = new Bundle();
            mNotes.setTitle(mBinding.etNotesTitle.getText().toString());
            mNotes.setClassName(mBinding.etNotesClass.getText().toString());
            mNotes.setPrice(Double.parseDouble(mBinding.etNotesPrice.getText().toString().trim()));
            bundle.putParcelable(Constants.NOTES, mNotes);
            bundle.putBoolean(Constants.IS_EDIT, true);
            bundle.putInt(Constants.POS, pos);
            ExplicitIntent.getsInstance().navigateTo(CreateNotesActivity.this, UploadAttachmentsActivity.class, bundle);
        } else {
            showProgressBar();
            addEditNoteRequest.setDetails("1");
            new BaseCallback<NoteResponse>(this, mRepository.getApiService().addNotes(addEditNoteRequest)) {

                @Override
                public void onSuccess(NoteResponse response) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.NOTES, response.getData());
                    bundle.putBoolean(Constants.IS_EDIT, false);
                    ExplicitIntent.getsInstance().navigateTo(CreateNotesActivity.this, UploadAttachmentsActivity.class, bundle);
                    //  finish();
                }

                @Override
                public void onFail() {
                    showToast("Failed");
                }
            };
        }
    }

    private boolean isValidNotesInfo() {
        return !StringUtils.isNullOrEmpty(mBinding.etNotesTitle.getText().toString().trim()) && !StringUtils.isNullOrEmpty(mBinding.etNotesClass.getText().toString().trim()) && !StringUtils.isNullOrEmpty(mBinding.etNotesPrice.getText().toString().trim()) && Double.parseDouble(mBinding.etNotesPrice.getText().toString().trim()) > 0.0;

    }

    private void setTextOutPut() {
        if (mBinding.etNotesClass.getText().toString().isEmpty()) {
            mBinding.etNotesClass.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.create_notes_selector));
        } else {
            mBinding.etNotesClass.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_selector_bg_radius_48));

        }

        if (mBinding.etNotesPrice.getText().toString().isEmpty()) {
            mBinding.etNotesPrice.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.create_notes_selector));
        } else {
            mBinding.etNotesPrice.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_selector_bg_radius_48));

        }

        if (mBinding.etNotesTitle.getText().toString().isEmpty()) {
            mBinding.etNotesTitle.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.create_notes_selector));
        } else {
            mBinding.etNotesTitle.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.white_selector_bg_radius_48));

        }
        checkIfVaild();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            setExistingTitleClassPrice((NotesModel) intent.getExtras().getParcelable(Constants.NOTES));
        }
    }

    private void setExistingTitleClassPrice(NotesModel notes) {
        mBinding.etNotesTitle.setText(notes.getTitle());
        mBinding.etNotesClass.setText(notes.getClassName());
        isEdit = true;
        mBinding.etNotesPrice.setText(String.valueOf(notes.getFormattedPrice()));
        mNotes = notes;

        mBinding.etNotesPrice.setSelection(mBinding.etNotesPrice.getText().length());
        setTextOutPut();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
    }

    @Override
    public void afterTextChanged(Editable s) {
        checkIfVaild();
    }


    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern pattern;

        public DecimalDigitsInputFilter(int digitsBeforeDecimal, int digitsAfterDecimal) {
            pattern = Pattern.compile("(([1-9]{1}[0-9]{0," + (digitsBeforeDecimal - 1) + "})?||[0]{1})((\\.[0-9]{0," + digitsAfterDecimal + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int sourceStart, int sourceEnd, Spanned destination, int destinationStart, int destinationEnd) {
            // Remove the string out of destination that is to be replaced.
            String newString = destination.toString().substring(0, destinationStart) + destination.toString().substring(destinationEnd, destination.toString().length());

            // Add the new string in.
            newString = newString.substring(0, destinationStart) + source.toString() + newString.substring(destinationStart, newString.length());

            // Now check if the new string is valid.
            Matcher matcher = pattern.matcher(newString);

            if (matcher.matches()) {
                // Returning null indicates that the input is valid.
                return null;
            }

            // Returning the empty string indicates the input is invalid.
            return "";
        }
    }

    private void checkIfVaild() {
        if (isValidNotesInfo()) {
            mBinding.header.tvHeaderEnd.setEnabled(true);
        } else {
            mBinding.header.tvHeaderEnd.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI(mBinding.clParent);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof CustomEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
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
