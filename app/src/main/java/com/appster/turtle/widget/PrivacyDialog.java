package com.appster.turtle.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.appster.turtle.R;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 17/01/18.
 */

public class PrivacyDialog extends Dialog {


    private Activity activity;
    private Selector selector;

    public PrivacyDialog(@NonNull Activity activity, Selector selector) {
        super(activity);
        this.activity = activity;
        this.selector = selector;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        PrivacyDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout. privacy_dialog, null, false);
        setContentView(R.layout.privacy_dialog);

//        binding.clPublic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                selector.onSelected(0);
//
//            }
//        });
//        binding.clPrivate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                selector.onSelected(1);
//            }
//        });
//        binding.clHidden.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                selector.onSelected(2);
//
//            }
//        });


    }


    public interface Selector {

        void onSelected(int i);
    }

}
