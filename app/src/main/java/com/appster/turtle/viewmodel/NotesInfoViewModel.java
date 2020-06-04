//package com.appster.turtle.viewmodel;
//
//import android.databinding.Observable;
//
//import com.appster.turtle.databinding.ActivityNotesInfoBinding;
//import com.appster.turtle.model.NotesModel;
//import com.appster.turtle.util.PreferenceUtil;
//
///**
// * Created by ashishkumar on 26/09/17.
// */
//
//public class NotesInfoViewModel implements Observable {
//
//    public NotesModel notesModel;
//    private ActivityNotesInfoBinding mBinding;
//    public String userName;
//
//    public NotesInfoViewModel(NotesModel notesModel, ActivityNotesInfoBinding mBinding) {
//        this.notesModel = notesModel;
//        this.mBinding = mBinding;
//        userName = PreferenceUtil.getUser().getValue();
//    }
//
//    @Override
//    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//
//        mBinding.ratingBar.setRating((notesModel.getAvgRating().floatValue()));
//    }
//
//    @Override
//    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//
//    }
//}
