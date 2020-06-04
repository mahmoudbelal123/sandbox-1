package com.appster.turtle.ui.meetup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.PlacesAdapter;
import com.appster.turtle.databinding.CreateMeetUpBinding;
import com.appster.turtle.model.Duration;
import com.appster.turtle.model.PlaceModel;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.request.CreateMeetupRequest;
import com.appster.turtle.network.response.CreateMeetupResponse;
import com.appster.turtle.network.response.DurationResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.Room;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.ui.rooms.AddTagsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.DatePickerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Dev on 06/03/18
 * fragment for meetup.
 */

public class CreateMeetUpFragment extends BaseFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private CreateMeetUpBinding mBinding;
    private CreateMeetupRequest mMeetupRequest = new CreateMeetupRequest();
    private PlacesAdapter adapter;
    private Room mRoom;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<AutocompletePrediction> mPlacesPredictionsList = new ArrayList<>();
    private PendingResult<AutocompletePredictionBuffer> results;
    private OnPlaceSelected onPlaceSelected;
    private PlaceModel mPlaceModel = new PlaceModel();
    private int durationId;
    private boolean isEditSelected;
    private AlertDialog dialogDuration;
    private boolean isSearching;
    private Calendar mCalenderDateAndTime;
    private String mSelectedAddress;
    private List<Duration> mDurationsList = new ArrayList<>();
    private Posts mPost;
    private List<Tag> tagList = Collections.emptyList();
    private boolean mForEdit;

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public CreateMeetUpFragment() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public static CreateMeetUpFragment newInstance() {
        return new CreateMeetUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.create_meet_up, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {


            mPost = bundle.getParcelable(Constants.BundleKey.POST);


            mForEdit = bundle.getBoolean(Constants.IS_EDIT, false);
            if (mForEdit) {
                mRoom = mPost.getRooms();

            } else {
                mRoom = bundle.getParcelable(Constants.BundleKey.ROOM);
            }

        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getBaseActivity(), 1, this)
                .build();
        initUI();
        StringUtils.greyTextPart(mBinding.tvText, ("Posting to " + mRoom.getValue()).toUpperCase(), mRoom.getValue().toUpperCase());


        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private void initUI() {

        mBinding.iBtnAddTags.setOnClickListener(this);
        onPlaceSelected = autocompletePrediction -> {
            mBinding.rcvLoc.setVisibility(View.GONE);
            isSearching = true;
            isEditSelected = false;
            findPlaceById(autocompletePrediction.getPlaceId(), autocompletePrediction.getFullText(null).toString());

            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    isSearching = false;

                }
            }.start();
        };
        mBinding.etTime.setOnClickListener(this);
        mBinding.etDuration.setOnClickListener(this);
        mBinding.ivCloseBtn.setOnClickListener(this);

        adapter = new PlacesAdapter(mPlacesPredictionsList, getContext(), onPlaceSelected);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rcvLoc.setLayoutManager(linearLayoutManager);
        mBinding.rcvLoc.setAdapter(adapter);


        mBinding.tvMeetUpTitle.setText(
                getString(R.string.at_symbol)
                        .concat(PreferenceUtil.getUser().getUserName())
                        .concat(getString(R.string.wants_to)));
        if (mForEdit) {
            mBinding.etActivity.setText(
                    mPost.getPostDetail().getTitle());
            isSearching = true;
            isEditSelected = true;
            mBinding.etDuration.setText(mPost.getPostDetail().getDuration());
            if (!mPost.getPostDetail().getAddress().isEmpty()) {
                mBinding.etLocation.setText(getString(R.string.meetup_location_prefix));
            }

            mBinding.etLocation.append(mPost.getPostDetail().getAddress());
            mPlaceModel.setAddress(mPost.getPostDetail().getAddress());
            mPlaceModel.setLatitude(mPost.getPostDetail().getLat());
            mPlaceModel.setLongitude(mPost.getPostDetail().getLon());
            mSelectedAddress = mPost.getPostDetail().getAddress();
            mBinding.etTime.setText(mPost.getPostDetail().getDataString());
            tagList = mPost.getTags();
            durationId = mPost.getPostDetail().getDurationId();
            mMeetupRequest.setDurationId(durationId);
            displayTags();
            setTextOutPut();
        }

        mBinding.etLocation.addTextChangedListener(textWatcher);

    }

    TextWatcher textWatcher = new TextWatcher() {
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

            loadData(editable.toString());

        }
    };

    private void setTextOutPut() {
        if (mBinding.etActivity.getText().toString().isEmpty()) {
            mBinding.etActivity.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.create_notes_selector));
        } else {
            mBinding.etActivity.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_selector_bg_radius_48));

        }

        if (mBinding.etDuration.getText().toString().isEmpty()) {
            mBinding.etDuration.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.create_notes_selector));
        } else {
            mBinding.etDuration.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_selector_bg_radius_48));

        }

        if (mBinding.etLocation.getText().toString().isEmpty()) {
            mBinding.etLocation.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.create_notes_selector));
        } else {
            mBinding.etLocation.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_selector_bg_radius_48));

        }
        if (mBinding.etTime.getText().toString().isEmpty()) {
            mBinding.etTime.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.create_notes_selector));
        } else {
            mBinding.etTime.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.white_selector_bg_radius_48));

        }

    }

    public void onAddTagClick() {
        Intent i = new Intent(getBaseActivity(), AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, (ArrayList<? extends Parcelable>) tagList);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iBtnAddTags:
                onAddTagClick();
                break;
            case R.id.etDuration:
                getDurations();
                break;
            case R.id.etTime:
                setDateTime();
                break;
            case R.id.ivCloseBtn:
                //TODO close the meet up fragment here
                getActivity().finish();
                break;
            default:
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getBaseActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:
                if (resultCode == RESULT_OK) {
                    tagList.clear();
                    tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                    displayTags();
                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    private void displayTags() {
        StringBuilder tags = new StringBuilder();
        for (Tag tag : tagList) {
            tags.append('#');
            tags.append(tag.getValue());
            tags.append(" ");
        }
        mBinding.tvTags.setText(tags.toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setMeetupPlace(Place place) {
        mPlaceModel.setAddress(mSelectedAddress);
        mPlaceModel.setLatitude(place.getLatLng().latitude);
        mPlaceModel.setLongitude(place.getLatLng().longitude);

        mBinding.etLocation.removeTextChangedListener(textWatcher);
        mBinding.etLocation.setText(getString(R.string.meetup_location_prefix));
        mBinding.etLocation.append(mSelectedAddress);

        mBinding.etLocation.addTextChangedListener(textWatcher);
    }

    public void findPlaceById(String id, final String fullText) {

        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(places -> {
            if (places.getStatus().isSuccess()) {
                Place place = places.get(0);
                mBinding.etLocation.clearFocus();
                mSelectedAddress = fullText;
                setMeetupPlace(place);

            }

            //Release the PlaceBuffer to prevent a memory leak
            places.release();
        });

    }


    public void postMeetUp() {


        showProgressBar();

        List<Integer> roomIdList = new ArrayList<>();

        mMeetupRequest.setTitle(mBinding.etActivity.getText().toString().trim());

        roomIdList.add(mRoom.getId());
        if (mForEdit) {
            mMeetupRequest.setPostId(mPost.getPostId()); //0 for new post, 1 for edited post
            if (mCalenderDateAndTime == null) {
                mMeetupRequest.setScheduleTime(mPost.getPostDetail().getScheduledTime());
            } else {
                mMeetupRequest.setScheduleTime(Utils.convertLocalTimeToUTCString(mCalenderDateAndTime));
            }
        } else {
            mMeetupRequest.setPostId(0);
            mMeetupRequest.setScheduleTime(Utils.convertLocalTimeToUTCString(mCalenderDateAndTime));
        }

        //}

        mMeetupRequest.setRoomIds(roomIdList);
        mMeetupRequest.setLat(mPlaceModel.getLatitude());
        mMeetupRequest.setLon(mPlaceModel.getLongitude());
        mMeetupRequest.setTags(tagList);


        if (!mBinding.etLocation.getText().toString().isEmpty()) {
            mMeetupRequest.setAddress(mSelectedAddress);

        }

        new BaseCallback<CreateMeetupResponse>(getBaseActivity(),
                ((ApplicationController) getContext().getApplicationContext()).provideRepository().getApiService().createMeetup(mMeetupRequest)) {

            @Override
            public void onSuccess(final CreateMeetupResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {

                    String message = mForEdit ? getString(R.string.meetup_edit_message) : getString(R.string.meetup_created_message);

                    Alert.createAlert(getContext(), "", message).setOnDismissListener(dialog -> {

                        getBaseActivity().hideProgressBar();
                        ((CreatePostActivity) getActivity()).finishActivity(response.getData().get(0));
                    }).show();
                }
            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };

    }

    void loadData(final String query) {

        mPlacesPredictionsList.clear();
        adapter.notifyDataSetChanged();

        if (results != null)
            results.cancel();


        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        results =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,
                        query,
                        new LatLngBounds(

                                new LatLng(-85, 180), new LatLng(85, -180)), filter
                );

        results.setResultCallback(autocompletePredictions -> {


            Log.d("Location", "Query" + query + " place " + mPlaceModel.getAddress());

            if (autocompletePredictions.getStatus().isSuccess()) {


                for (AutocompletePrediction prediction : autocompletePredictions) {
                    //Add as a new item to avoid IllegalArgumentsException when buffer is released
                    mPlacesPredictionsList.add(0, prediction);
                }
            } else if (mPlacesPredictionsList.size() == 0) {
                mBinding.rcvLoc.setVisibility(View.GONE);
            }

            if (query.isEmpty()) {
                mPlacesPredictionsList.clear();
                mBinding.rcvLoc.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return;
            } else {
                String finalQuery = query;
                if (finalQuery.startsWith("at")) {
                    finalQuery = finalQuery.substring(finalQuery.indexOf(" ") + 1);
                }
                isEditSelected = finalQuery.equals(mPlaceModel.getAddress());
                if (!isEditSelected) {
                    mBinding.rcvLoc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.rcvLoc.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }


        });

    }

    private void initDurationPicker() {
        if (mDurationsList == null || mDurationsList.size() == 0)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Select duration");
        View dialogView = getLayoutInflater().inflate(R.layout.layout_number_picker, null);
        builder.setView(dialogView);

        final NumberPicker npDuration = dialogView.findViewById(R.id.number_picker);

        String[] durationValues = new String[mDurationsList.size()];
        int pos = 0;
        for (int i = 0; i < durationValues.length; i++) {
            durationValues[i] = mDurationsList.get(i).getValue();
            if (mDurationsList.get(i).getId() == durationId) {
                pos = i;
            }
        }


        npDuration.setMinValue(0);
        npDuration.setMaxValue(mDurationsList.size() - 1);
        npDuration.setWrapSelectorWheel(false);
        npDuration.setDisplayedValues(durationValues);
        npDuration.setValue(pos);

        builder.setPositiveButton(getString(R.string.set), (dialogInterface, i) -> setMeetupDuration(npDuration.getValue()));

        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

        });

        dialogDuration = builder.create();
        dialogDuration.show();
    }

    private void setMeetupDuration(int positionSelected) {
        mBinding.etDuration.setText(R.string.for_str);
        mBinding.etDuration.append(mDurationsList.get(positionSelected).getValue());
        mMeetupRequest.setDurationId(mDurationsList.get(positionSelected).getId());
        //   checkMeetupValidity();
        durationId = mDurationsList.get(positionSelected).getId();
    }

    private void launchDurationSelector() {
        if (dialogDuration != null)
            dialogDuration.show();
    }


    public boolean checkMeetupValidity() {
        //validate meetup title
        if (mBinding.etActivity.getText().toString().trim().isEmpty()) {
            showSnackBar("Activity canot be empty");
            return false;
        }

        //validate meetup date and time
        if (mBinding.etTime.getText().toString().trim().length() == 0) {
            showSnackBar("Time cannot be empty ");
            return false;
        }

        //validate meetup duration
        if (mBinding.etDuration.getText().toString().trim().length() < 3) {
            showSnackBar("Please choose duration ");
            return false;
        }

        //validate meetup place

        return true;
    }

    private void getDurations() {

        showProgressBar();

        new BaseCallback<DurationResponse>(getBaseActivity(), ((ApplicationController) (getContext().getApplicationContext()))
                .provideRepository().getApiService().getDurations()) {

            @Override
            public void onSuccess(DurationResponse response) {
                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    mDurationsList.addAll(response.getData());
                    initDurationPicker();


                }
            }

            @Override
            public void onFail() {
                showToast("Error fetching durations");
                getBaseActivity().hideProgressBar();
            }
        };

    }

    public interface OnPlaceSelected {
        void placeSelected(AutocompletePrediction autocompletePrediction);
    }

    public void setDateTime() {
        selectDate();
    }

    public void selectDate() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            LogUtils.LOGI(getFragmentName(), year + ":" + month + ":" + day);
            mCalenderDateAndTime = Calendar.getInstance();
            mCalenderDateAndTime.set(Calendar.YEAR, year);
            mCalenderDateAndTime.set(Calendar.MONTH, month);
            mCalenderDateAndTime.set(Calendar.DAY_OF_MONTH, day);
            selectTime();
            hideKeyboard(getView());
        };
        datePickerFragment.setOnDateSetListener(dateSetListener);
        datePickerFragment.show(getFragmentManager(), getFragmentName());
    }

    public void selectTime() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hourOfDay, minute) -> {
            LogUtils.LOGI(getFragmentName(), hourOfDay + ":" + minute);
            mCalenderDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalenderDateAndTime.set(Calendar.MINUTE, minute);

            if (mCalenderDateAndTime.getTimeInMillis() <= System.currentTimeMillis()) {
                showToast(getString(R.string.invalid_time_error));
                return;
            }
            hideKeyboard(getView());
            setMeetupDateAndTime();
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                timeSetListener,
                Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR),
                Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void setMeetupDateAndTime() {
        mBinding.etTime.setText("");

        int year = mCalenderDateAndTime.get(Calendar.YEAR);
        String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(mCalenderDateAndTime.getTime());
        int dayOfMonth = mCalenderDateAndTime.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat mTimeSDF = new SimpleDateFormat("h:mmaa", Locale.getDefault());
        String time = mTimeSDF.format(mCalenderDateAndTime.getTime());

        mBinding.etTime.setText(getFormattedMeetupDate(dayOfMonth, month, year, time));
        //  checkMeetupValidity();
    }

    private String getFormattedMeetupDate(int dayOfMonth, String month, int year, String time) {
        return "at " + time.toLowerCase() + " on " + month + " " + dayOfMonth + Utils.getDayOfMonthSuffix(dayOfMonth) + ", " + year;
    }


}