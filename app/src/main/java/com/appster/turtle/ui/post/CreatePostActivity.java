package com.appster.turtle.ui.post;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCreatePostBinding;
import com.appster.turtle.databinding.LayoutGallerySelectorBinding;
import com.appster.turtle.model.Event;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.media.CameraActivity;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.appster.turtle.ui.meetup.CreateMeetUpFragment;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created  on 05/03/18 .
 * create poll activity
 */

public class CreatePostActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, BaseActivity.PermissionI, View.OnClickListener {
    ActivityCreatePostBinding mBinding;


    private CreatePollPostFragment pollFragment;
    private CreateTextPostFragment textPostFragment;
    private CreateMeetUpFragment mCreateMeetUpFragment;
    private LayoutGallerySelectorBinding bottomSheetBinding;
    private boolean onPermissionGranted;
    private Posts post;
    private Room room;
    private int lastChecked = 0;
    private int roomId;
    private boolean isEdit, isFromRoom;

    public CreatePollPostFragment getPollFragment() {
        return pollFragment;
    }

    public CreateTextPostFragment getTextPostFragment() {
        return textPostFragment;
    }

    public CreateMeetUpFragment getmCreateMeetUpFragment() {
        return mCreateMeetUpFragment;
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_post);

        roomId = getIntent().getExtras().getInt(Constants.BundleKey.ROOM_ID, -1);
        room = getIntent().getExtras().getParcelable(Constants.BundleKey.ROOM);
        isEdit = getIntent().getExtras().getBoolean(Constants.IS_EDIT, false);
        isFromRoom = getIntent().getExtras().getBoolean(Constants.IS_FROM_ROOM, false);
        if (getIntent().hasExtra(Constants.BundleKey.POST)) {
            post = getIntent().getParcelableExtra(Constants.BundleKey.POST);
        }
        mBinding.radioGroup.setOnCheckedChangeListener(this);
        mBinding.ibPost.setOnClickListener(this);

        lastChecked = mBinding.rbText.getId();
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(CreatePostActivity.this), R.layout.layout_gallery_selector, mBinding.bottomSheet, false);

        lastChecked = mBinding.rbText.getId();

        bottomSheetBinding.rlHeader.setVisibility(View.VISIBLE);
        ((View) bottomSheetBinding.rlHeader.getParent()).setBackgroundResource(R.drawable.white_alpha_upr_rounded);
        StringUtils.greyTextPart(bottomSheetBinding.tvText, (getString(R.string.posting_to) + room.getValue()).toUpperCase(), room.getValue().toUpperCase());

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        displayFragment();

        bottomSheetBinding.ivCloseBottom.setOnClickListener(view -> mBinding.bottomSheet.dismissSheet());
        mBinding.bottomSheet.setPeekSheetTranslation(Utils.dpToPx(CreatePostActivity.this, 500f));
        if (isEdit) {
            mBinding.radioGroup.setVisibility(View.GONE);
            LogUtils.LOGD("this ", post.getRooms().getValue());
            if (post.getPostType() == Constants.VIEW_TYPE.TEXT) {
                mBinding.rbText.setChecked(true);
                displayFragment();
            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                mBinding.rbMeetUp.setChecked(true);
                displayMeetUpFragment();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        hideKeyboard();

        switch (checkedId) {
            case R.id.rb_text:
                lastChecked = checkedId;
                displayFragment();
                break;
            case R.id.rb_poll:
                lastChecked = checkedId;

                displayPollFragment();
                break;
            case R.id.rb_meetUp:
                lastChecked = checkedId;

                displayMeetUpFragment();
                break;
            case R.id.rb_camera:
                toggleMediaBottom();
                break;
            default:
                break;
        }
    }

    private void displayFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (textPostFragment != null) {
            fragmentTransaction.show(textPostFragment);

            if (pollFragment != null)
                fragmentTransaction.hide(pollFragment);

            if (mCreateMeetUpFragment != null)
                fragmentTransaction.hide(mCreateMeetUpFragment);
            fragmentTransaction.commit();


        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);
            bundle.putParcelable(Constants.BundleKey.POST, post);
            bundle.putParcelable(Constants.BundleKey.ROOM, room);

            if (isEdit) {
                bundle.putBoolean(Constants.IS_EDIT, true);
            }
            textPostFragment = CreateTextPostFragment.newInstance();
            textPostFragment.setArguments(bundle);
            fragmentTransaction
                    .add(R.id.fl_container, textPostFragment, "TEXT");
            fragmentTransaction.commit();
        }
    }


    private void displayPollFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (pollFragment != null) {
            fragmentTransaction.show(pollFragment);

            if (textPostFragment != null)
                fragmentTransaction.hide(textPostFragment);

            if (mCreateMeetUpFragment != null)
                fragmentTransaction.hide(mCreateMeetUpFragment);

            fragmentTransaction.commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            pollFragment = CreatePollPostFragment.newInstance();
            pollFragment.setArguments(bundle);
            fragmentTransaction
                    .add(R.id.fl_container, pollFragment, "POLL");
            fragmentTransaction.commit();


        }
    }

    private void displayMeetUpFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (mCreateMeetUpFragment != null) {
            fragmentTransaction.show(mCreateMeetUpFragment);

            if (textPostFragment != null)
                fragmentTransaction.hide(textPostFragment);

            if (pollFragment != null)
                fragmentTransaction.hide(pollFragment);

            fragmentTransaction.commit();
        } else {
            mCreateMeetUpFragment = CreateMeetUpFragment.newInstance();
            fragmentTransaction
                    .add(R.id.fl_container, mCreateMeetUpFragment, getString(R.string.meetUptag));
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);


            if (isEdit) {
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
            }
            mCreateMeetUpFragment.setArguments(bundle);
            //
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onClick(View v) {
        if (mBinding.rbText.isChecked()) {
            if (textPostFragment.isValidate()) {
                textPostFragment.postClicked();
            }
        } else if (mBinding.rbPoll.isChecked()) {
            if (pollFragment.isVaildate()) {
                pollFragment.postPoll();
                for (int i = 0; i < pollFragment.getmPoll().getPollAnswersList().size(); i++) {
                    LogUtils.LOGD("TAG1 ", pollFragment.getmPoll().getPollAnswersList().get(i).getAnswer() + "  j" + pollFragment.getmPoll().getPollAnswersList().get(i).getAnswerOrder());
                }
            }
        } else if (mBinding.rbMeetUp.isChecked()) {
            if (mCreateMeetUpFragment.checkMeetupValidity()) {
                mCreateMeetUpFragment.postMeetUp();
            }
        }
    }

    private void toggleMediaBottom() {

        if (onPermissionGranted) {
            if (getFragment(MediaBottomFragment.class.getName()) == null) {
                Bundle b = new Bundle();

                b.putBoolean(MediaBottomFragment.IS_ALL, true);

                pushFragment(Constants.FRAGMENTS.MEDIA_BOTTOM_FRAGMENT, b, bottomSheetBinding.flMediaBottomSheet.getId(), false, ANIMATION_TYPE.NONE);
            }

            mBinding.bottomSheet.addOnSheetStateChangeListener(state -> {

                if (state.equals(BottomSheetLayout.State.PREPARING)) {
                    if (getFragment(MediaBottomFragment.class.getName()) != null)
                        ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).start();
                }

                if (state.equals(BottomSheetLayout.State.HIDDEN)) {
                    ((RadioButton) CreatePostActivity.this.findViewById(lastChecked)).setChecked(true);
                    mBinding.rlRoot.setVisibility(View.VISIBLE);
                }
            });

            mBinding.rlRoot.setVisibility(View.INVISIBLE);
            mBinding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());

        } else
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


    }

    @Override
    public void onPermissionGiven() {
        onPermissionGranted = true;
        toggleMediaBottom();
    }


    @Subscribe
    public void onEvent(Event event) {

        switch (event.getEventId()) {
            case Constants.eventI.ON_CAMERA_CLICK:
                mBinding.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);

                ExplicitIntent.getsInstance().navigateTo(CreatePostActivity.this, CameraActivity.class, bundle);
                CreatePostActivity.this.finish();


                break;

            case Constants.eventI.ON_IMAGE_CLICK:
                mBinding.bottomSheet.dismissSheet();

                bundle = new Bundle();

                bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);

                if (event.getEventMsg() != null)
                    bundle.putString(Constants.BundleKey.IMAGE_URL, event.getEventMsg());
                ExplicitIntent.getsInstance().navigateTo(CreatePostActivity.this, CameraActivity.class, bundle);
                CreatePostActivity.this.finish();

                break;

        }
    }


    @Override
    protected void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public void finishActivity(Posts posts) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.ROOM_ID, roomId);
        bundle.putParcelable(Constants.BundleKey.POST, posts);


        Intent i = new Intent();
        if (isFromRoom) {
            i.putExtras(bundle);
            setResult(RESULT_OK, i);
            finish();

        } else {
            bundle.putBoolean(Constants.IS_EDIT, true);
            ExplicitIntent.getsInstance().navigateTo(this, HomeMainActivity.class, bundle);

        }
    }

    public ActivityCreatePostBinding getmBinding() {
        return mBinding;
    }
}
