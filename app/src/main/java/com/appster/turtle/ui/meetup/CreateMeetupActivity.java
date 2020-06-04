/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.meetup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PlacesPredictionAdapter;
import com.appster.turtle.databinding.ActivityCreateMeetupBinding;
import com.appster.turtle.model.Duration;
import com.appster.turtle.model.PlaceModel;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.request.CreateMeetupRequest;
import com.appster.turtle.network.response.CreateMeetupResponse;
import com.appster.turtle.network.response.DurationResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.AddTagsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.appster.turtle.util.LogUtils.LOGE;

/**
 * A activity class for meetup
 */
public class CreateMeetupActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private ActivityCreateMeetupBinding mBinding;
    private Calendar mCalenderDateAndTime;
    private List<Duration> mDurationsList;
    private AlertDialog dialogDuration;
    private PlaceModel mPlaceModel;
    private Room mRoom;

    private CreateMeetupRequest mMeetupRequest;

    private ArrayList<Tag> tagList;
    private boolean mForEdit;
    private Posts mPost;

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<AutocompletePrediction> mPlacesPredictionsList;
    private PlacesPredictionAdapter mPlacesPredictionsAdapter;
    private String mSelectedAddress;

    @Override
    public String getActivityName() {
        return CreateMeetupActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_meetup);

        Intent i = getIntent();
        mRoom = i.getParcelableExtra(Constants.BundleKey.ROOM);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mMeetupRequest = new CreateMeetupRequest();
        tagList = new ArrayList<>();


        if (i.hasExtra(Constants.BundleKey.FOR_EDIT)) {
            mForEdit = i.getBooleanExtra(Constants.BundleKey.FOR_EDIT, false);
            mPost = i.getParcelableExtra(Constants.BundleKey.MEETUP);
        }

        initUI();


    }

    private void initUI() {

        mPlacesPredictionsList = new ArrayList<>();
        mDurationsList = new ArrayList<>();

        initPlacesSuggestionsRecyclerView();

        getDurations();

        mBinding.etMeetupTitle.addTextChangedListener(meetupTitleTextWatcher);
        mBinding.clHeader.tvHeaderEnd.setOnClickListener(this);
//        mBinding.tvLocation.setOnClickListener(this);
        mBinding.tvLocation.addTextChangedListener(placesTextWatcher);
        mBinding.tvLocation.setOnFocusChangeListener(focusChangeListener);
        mBinding.clHeader.ivIconStart.setOnClickListener(this);
        mBinding.tvTimeAndDate.setOnClickListener(this);
        mBinding.tvDuration.setOnClickListener(this);

        //set username meetup string at top of the 4 fields
        mBinding.tvMeetupCreator.setText(StringUtils.getFullString(getString(R.string.at_symbol), PreferenceUtil.getUser().getUserName(), getString(R.string.meetup_creator_placeholder)));

        //set header views
        if (mForEdit)
            setUpHeader(mBinding.clHeader.clHeader, getString(R.string.edit_meetup), getString(R.string.done));
        else
            setUpHeader(mBinding.clHeader.clHeader, getString(R.string.create_meetup), getString(R.string.post));


        if (mForEdit) {
            mBinding.etMeetupTitle.setText(mPost.getPostDetail().getTitle());
            mBinding.tvTimeAndDate.setText(mPost.getPostDetail().getDateFormatted());
            mBinding.tvDuration.setText(String.format(" for %s", mPost.getPostDetail().getDuration()));
            mBinding.tvTagDesc.setText(mPost.getFormattedTags());
            mPlaceModel = new PlaceModel();
            mPlaceModel.setAddress(mPost.getPostDetail().getAddress());
            mPlaceModel.setLatitude(mPost.getPostDetail().getLat());
            mPlaceModel.setLongitude(mPost.getPostDetail().getLon());

            tagList.addAll(mPost.getTags());

            mBinding.tvLocation.setText(R.string.meetup_location_prefix);
            mBinding.tvLocation.append(mPlaceModel.getAddress());

            mMeetupRequest.setTitle(mBinding.etMeetupTitle.getText().toString().trim());

        }

    }

    private void initDurationPicker() {
        if (mDurationsList == null || mDurationsList.size() == 0)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Select duration");
        View dialogView = getLayoutInflater().inflate(R.layout.layout_number_picker, null);
        builder.setView(dialogView);

        final NumberPicker npDuration = dialogView.findViewById(R.id.number_picker);

        String[] durationValues = new String[mDurationsList.size()];

        for (int i = 0; i < durationValues.length; i++) {
            durationValues[i] = mDurationsList.get(i).getValue();
        }


        npDuration.setMinValue(0);
        npDuration.setMaxValue(mDurationsList.size() - 1);
        npDuration.setWrapSelectorWheel(false);
        npDuration.setDisplayedValues(durationValues);

        builder.setPositiveButton(getString(R.string.set), (dialogInterface, i) -> setMeetupDuration(npDuration.getValue()));

        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

        });

        dialogDuration = builder.create();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOGE(getActivityName(), "ConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onClick(View view) {

        //to change editText background to default
        mBinding.etMeetupTitle.clearFocus();

        switch (view.getId()) {
            case R.id.tv_time_and_date:
                launchDateAndTimePicker();
                break;

            case R.id.tv_duration:
                launchDurationSelector();
                break;

            case R.id.tv_location:
                mBinding.tvLocation.requestFocus();
//                launchPlaceSelector();
                break;

            case R.id.tv_header_end:
                postMeetup();
                break;

            case R.id.iv_icon_start:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.PLACES_SELECTOR_REQUEST_CODE:
                mBinding.tvLocation.setClickable(true);

                break;

            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:
                if (resultCode == RESULT_OK) {

                    tagList.clear();
                    tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                    displayTags();

                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                    showToast(getString(R.string.cancelled));
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mCalenderDateAndTime = Calendar.getInstance();
        mCalenderDateAndTime.set(Calendar.YEAR, year);
        mCalenderDateAndTime.set(Calendar.MONTH, month);
        mCalenderDateAndTime.set(Calendar.DAY_OF_MONTH, day);

        showTimePickerDialog();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalenderDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalenderDateAndTime.set(Calendar.MINUTE, minute);

        if (mCalenderDateAndTime.getTimeInMillis() <= System.currentTimeMillis()) {
            showToast(getString(R.string.invalid_time_error));
            return;
        }

        setMeetupDateAndTime();
    }

    private void launchDurationSelector() {
        if (dialogDuration != null)
            dialogDuration.show();
    }

    private void launchDateAndTimePicker() {
        showDatePickerDialog();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateMeetupActivity.this, this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMeetupActivity.this, this, hour, minute, false);
        timePickerDialog.show();
    }

    private void setMeetupDateAndTime() {
        mBinding.tvTimeAndDate.setText("");

        int year = mCalenderDateAndTime.get(Calendar.YEAR);
        String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(mCalenderDateAndTime.getTime());
        int dayOfMonth = mCalenderDateAndTime.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat mTimeSDF = new SimpleDateFormat("h:mmaa", Locale.getDefault());
        String time = mTimeSDF.format(mCalenderDateAndTime.getTime());

        mBinding.tvTimeAndDate.setText(getFormattedMeetupDate(dayOfMonth, month, year, time));
        checkMeetupValidity();
    }

    private void setMeetupDuration(int positionSelected) {
        mBinding.tvDuration.setText(R.string.for_str);
        mBinding.tvDuration.append(mDurationsList.get(positionSelected).getValue());
        mMeetupRequest.setDurationId(mDurationsList.get(positionSelected).getId());
        checkMeetupValidity();
    }


    private void setMeetupPlace(Place place) {

        mPlaceModel = new PlaceModel();
        mPlaceModel.setAddress(mSelectedAddress);
        mPlaceModel.setLatitude(place.getLatLng().latitude);
        mPlaceModel.setLongitude(place.getLatLng().longitude);

        mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
        mBinding.clHeader.tvHeaderEnd.setEnabled(false);

        mBinding.tvLocation.setText(getString(R.string.meetup_location_prefix));
        mBinding.tvLocation.append(mSelectedAddress);

        checkMeetupValidity();
    }

    private String getFormattedMeetupDate(int dayOfMonth, String month, int year, String time) {
        return "at " + time.toLowerCase() + " on " + month + " " + dayOfMonth + Utils.getDayOfMonthSuffix(dayOfMonth) + ", " + year;
    }

    private void getDurations() {

        showProgressBar();

        new BaseCallback<DurationResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().getDurations()) {

            @Override
            public void onSuccess(DurationResponse response) {
                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    mDurationsList.addAll(response.getData());
                    hideProgressBar();
                    initDurationPicker();

                    if (mForEdit)
                        for (Duration duration : mDurationsList) {
                            if (duration.getValue().equals(mPost.getPostDetail().getDuration())) {
                                mMeetupRequest.setDurationId(duration.getId());
                                break;

                            }
                        }

                }
            }

            @Override
            public void onFail() {
                showToast("Error fetching durations");
                hideProgressBar();
            }
        };

    }

    TextWatcher meetupTitleTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkMeetupValidity();
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private void checkMeetupValidity() {
        //validate meetup title
        if (mBinding.etMeetupTitle.getText().toString().trim().isEmpty()) {
            mBinding.clHeader.tvHeaderEnd.setEnabled(false);
            return;
        }

        //validate meetup date and time
        if (mBinding.tvTimeAndDate.getText().toString().trim().length() == 0) {
            mBinding.clHeader.tvHeaderEnd.setEnabled(false);
            return;
        }

        //validate meetup duration
        if (mBinding.tvDuration.getText().toString().trim().length() == 0) {
            mBinding.clHeader.tvHeaderEnd.setEnabled(false);
            return;
        }

        //validate meetup place
        if (mBinding.tvLocation.getText().toString().trim().length() == 0) {
            mBinding.clHeader.tvHeaderEnd.setEnabled(false);
            return;
        }


        mBinding.clHeader.tvHeaderEnd.setEnabled(true);
    }

    public void onAddTagClick(View view) {

        Intent i = new Intent(CreateMeetupActivity.this, AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, tagList);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);

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

        mBinding.tvTagDesc.setText(tagStr.toString());
        if (mForEdit && (tagStr.length() > 0)) {
            checkMeetupValidity();
        }
    }

    private void postMeetup() {

        if (mSelectedAddress == null || !mBinding.tvLocation.getText().toString().trim().equals(getString(R.string.meetup_location_prefix) + mSelectedAddress)) {
            showSnackBar("Invalid location. Please re-select location.");
            return;
        }

        showProgressBar();

        List<Integer> roomIdList = new ArrayList<>();

        mMeetupRequest.setTitle(mBinding.etMeetupTitle.getText().toString().trim());

        if (mForEdit) {
            mMeetupRequest.setPostId(mPost.getPostId());
            roomIdList.add(mPost.getRooms().getId());
            mMeetupRequest.setScheduleTime(mPost.getPostDetail().getScheduledAt());
        } else {
            roomIdList.add(mRoom.getRoomId());
            mMeetupRequest.setPostId(0); //0 for new post, 1 for edited post
            mMeetupRequest.setScheduleTime(Utils.convertLocalTimeToUTCString(mCalenderDateAndTime));
        }

        mMeetupRequest.setRoomIds(roomIdList);
        mMeetupRequest.setLat(mPlaceModel.getLatitude());
        mMeetupRequest.setLon(mPlaceModel.getLongitude());
        mMeetupRequest.setTags(tagList);

        mMeetupRequest.setAddress(mSelectedAddress);

        new BaseCallback<CreateMeetupResponse>(CreateMeetupActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().createMeetup(mMeetupRequest)) {

            @Override
            public void onSuccess(CreateMeetupResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {

                    String message = mForEdit ? getString(R.string.meetup_edit_message) : getString(R.string.meetup_created_message);

                    Alert.createAlert(CreateMeetupActivity.this, "", message).setOnDismissListener(dialog -> {

                        hideProgressBar();
                        finish();

                    }).show();
                }
            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };

    }

    private void initPlacesSuggestionsRecyclerView() {
        mBinding.rvPlacesSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPlacesSuggestions.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 10)));
        mPlacesPredictionsAdapter = new PlacesPredictionAdapter(this, mPlacesPredictionsList);
        mBinding.rvPlacesSuggestions.setAdapter(mPlacesPredictionsAdapter);
    }

    TextWatcher placesTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!mBinding.tvLocation.hasFocus() || charSequence.toString().trim().startsWith("at ") ||
                    charSequence.length() == 0) {
                mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
            } else {
                mPlacesPredictionsList.clear();
                mPlacesPredictionsAdapter.notifyDataSetChanged();
                mBinding.rvPlacesSuggestions.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (charSequence.length() == 0) {
                mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
                return;
            }

            mPlacesPredictionsList.clear();

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,
                            charSequence.toString(),
                            new LatLngBounds(
                                    new LatLng(-33.880490, 151.184363),
                                    new LatLng(-33.858754, 151.229596)),
                            null);

            results.setResultCallback(autocompletePredictions -> {

                if (autocompletePredictions.getStatus().isSuccess()) {

                    mBinding.rvPlacesSuggestions.setVisibility(View.VISIBLE);

                    for (AutocompletePrediction prediction : autocompletePredictions) {
                        //Add as a new item to avoid IllegalArgumentsException when buffer is released
                        mPlacesPredictionsList.add(0, prediction);
                    }
                } else if (mPlacesPredictionsList.size() == 0) {
                    mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
                }

                mPlacesPredictionsAdapter.notifyDataSetChanged();
            });
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (mPlacesPredictionsList.size() > 0) {
                mBinding.rvPlacesSuggestions.setVisibility(View.VISIBLE);
            } else {
                mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
            }

        }
    };

    public void findPlaceById(String id, final String fullText) {

        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(places -> {
            if (places.getStatus().isSuccess()) {
                Place place = places.get(0);
                mBinding.tvLocation.clearFocus();
                mBinding.tvLocation.removeTextChangedListener(placesTextWatcher);
                mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
                mBinding.clHeader.tvHeaderEnd.requestFocus();
                mSelectedAddress = fullText;
                setMeetupPlace(place);
            }

            //Release the PlaceBuffer to prevent a memory leak
            places.release();
        });

    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mBinding.tvLocation.addTextChangedListener(placesTextWatcher);
            } else {
                mBinding.rvPlacesSuggestions.setVisibility(View.GONE);
                mBinding.tvLocation.removeTextChangedListener(placesTextWatcher);
            }
        }
    };
}

