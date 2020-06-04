package com.appster.turtle.ui.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.FragmentHomeMainBinding;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.search.SearchActivity;
import com.appster.turtle.util.LogUtils;
/**
 * A fragment class for home main
 */
public class HomeMainFragment extends BaseFragment implements View.OnClickListener {
    private static final String IS_HOME = "IS_HOME";
    private int selectedScreen = Constants.HOME_CURRENT_SCREEN.HOT;// 1-hot, 2-new, 3-top
    private boolean isHome;
    private OnFragmentInteractionListener mListener;
    private FragmentHomeMainBinding binding;
    private HomeFeedFragment hotFragment, topFragment, newFragment;
    private Context mContext;

    public HomeMainFragment() {
        //
    }

    public static HomeMainFragment newInstance(boolean isHome) {
        HomeMainFragment fragment = new HomeMainFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_HOME, isHome);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentName() {
        return HomeMainFragment.class.getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isHome = getArguments().getBoolean(IS_HOME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_main, container, false);
        LogUtils.LOGD("FRAGMENT", "HOME -" + isHome);

        initUI();
        return binding.getRoot();
    }

    public void refreshData() {

        if (hotFragment != null)
            hotFragment.dataRefresh();

        if (newFragment != null)
            newFragment.dataRefresh();

        if (topFragment != null)
            topFragment.dataRefresh();
    }

    public void refreshRoomData(int pos) {


        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {

            hotFragment.dataRoomRefresh(pos);

            if (newFragment != null)
                newFragment.dataRefresh();

            if (topFragment != null)
                topFragment.dataRefresh();

        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {


            newFragment.dataRoomRefresh(pos);

            if (hotFragment != null)
                hotFragment.dataRefresh();

            if (topFragment != null)
                topFragment.dataRefresh();

        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.TOP) {

            topFragment.dataRoomRefresh(pos);

            if (newFragment != null)
                newFragment.dataRefresh();

            if (hotFragment != null)
                hotFragment.dataRefresh();


        }

    }

    private void initUI() {

        binding.tvTop.setOnClickListener(this);
        binding.tvNew.setOnClickListener(this);
        binding.tvHot.setOnClickListener(this);
        binding.ivMenu.setOnClickListener(this);
        binding.ivSearch.setOnClickListener(this);


        hotFragment = HomeFeedFragment.newInstance(isHome, Constants.HOME_CURRENT_SCREEN.HOT);


        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();
        fragmentTransaction
                .add(R.id.fl_container, hotFragment);
        fragmentTransaction.commitAllowingStateLoss();

        fragmentTransaction.show(hotFragment);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_search:

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);

                break;
            case R.id.tv_hot:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.HOT;
                switchScreen();
                break;
            case R.id.tv_new:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.NEW;
                switchScreen();

                break;
            case R.id.tv_top:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.TOP;
                switchScreen();

                break;
            case R.id.iv_menu:
                ((HomeMainActivity) getActivity()).toggleDrawerLayout();
                break;

            default:
                break;
        }
    }

    private void switchScreen() {

        binding.tvHot.setTextColor(ContextCompat.getColor(getActivity(), selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT ? R.color.bright_teal : R.color.black));
        binding.tvTop.setTextColor(ContextCompat.getColor(getActivity(), selectedScreen == Constants.HOME_CURRENT_SCREEN.TOP ? R.color.bright_teal : R.color.black));
        binding.tvNew.setTextColor(ContextCompat.getColor(getActivity(), selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW ? R.color.bright_teal : R.color.black));

        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();


        if (hotFragment != null)
            fragmentTransaction.hide(hotFragment);

        if (topFragment != null)
            fragmentTransaction.hide(topFragment);

        if (newFragment != null)
            fragmentTransaction.hide(newFragment);


        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            fragmentTransaction.show(hotFragment);

        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {

            if (newFragment == null) {
                newFragment = HomeFeedFragment.newInstance(isHome, Constants.HOME_CURRENT_SCREEN.NEW);
                fragmentTransaction.add(R.id.fl_container, newFragment);

            }

            fragmentTransaction.show(newFragment);


        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.TOP) {

            if (topFragment == null) {
                topFragment = HomeFeedFragment.newInstance(isHome, Constants.HOME_CURRENT_SCREEN.TOP);
                fragmentTransaction.add(R.id.fl_container, topFragment);

            }
            fragmentTransaction.show(topFragment);


        }


        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
    }

    public void updatePost(Posts posts) {
        if (hotFragment != null) {
            hotFragment.updatePost(posts, true, false);
        }

        if (newFragment != null) {
            newFragment.updatePost(posts, true, false);

        }

        if (topFragment != null) {
            topFragment.updatePost(posts, true, false);

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void deletePost(Posts post, int position) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.deletePost(post, position);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.deletePost(post, position);
        } else {
            topFragment.deletePost(post, position);
        }
    }


    public void onPollClick(int position, int postId, int answerId) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onPollClick(position, postId, answerId);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onPollClick(position, postId, answerId);
        } else {
            topFragment.onPollClick(position, postId, answerId);
        }
    }

    public void onMeetupStatusChange(int position, int postId, int status, boolean isMeetupResponded) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        } else {
            topFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        }
    }

    public void onLiked(int position, Posts postId, int likeStatus, int alreadyStatus) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        } else {
            topFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        }
    }

    public int getSelectedScreen() {
        return selectedScreen;
    }
}
