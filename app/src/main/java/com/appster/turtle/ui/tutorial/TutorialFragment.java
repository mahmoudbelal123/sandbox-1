package com.appster.turtle.ui.tutorial;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.databinding.FragmentTutorialBinding;
import com.appster.turtle.ui.Constants;

/**
 * Created by atul on 13/09/17.
 * To inject activity reference.
 *
 * fragment for tutorial
 */

public class TutorialFragment extends Fragment {

    private FragmentTutorialBinding tutorialBinding;

    public static TutorialFragment newInstance(Bundle bundle) {

        TutorialFragment instance = new TutorialFragment();
        instance.setArguments(bundle);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tutorialBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial, container, false);
        setUpViews();
        return tutorialBinding.getRoot();
    }

    private void setUpViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean(Constants.BundleKey.TXT_SIZE_CHANGE)) {
                tutorialBinding.tvMsgTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 57.6f);
            }
            switch (bundle.getInt(Constants.BundleKey.TXT_ID)) {
                case 2:
                    setSpannableString(getActivity().getString(bundle.getInt(Constants.BundleKey.TXT_RES_ID)), 23, 39, tutorialBinding.tvMsgTxt);
                    break;
                case 3:
                    setSpannableString(getActivity().getString(bundle.getInt(Constants.BundleKey.TXT_RES_ID)), 16, 42, tutorialBinding.tvMsgTxt);
                    break;
                case 4:
                    setSpannableString(getActivity().getString(bundle.getInt(Constants.BundleKey.TXT_RES_ID)), 14, 48, tutorialBinding.tvMsgTxt);
                    break;
                default:
                    tutorialBinding.tvMsgTxt.setText(bundle.getInt(Constants.BundleKey.TXT_RES_ID));
                    break;
            }

        }
    }

    private void setSpannableString(String str, int start, int end, TextView view) {
        SpannableString styledString
                = new SpannableString(str);
        styledString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.bright_teal)), start, end, 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(styledString, TextView.BufferType.SPANNABLE);
        view.setSelected(true);
    }

}
