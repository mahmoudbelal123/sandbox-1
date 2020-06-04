/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class PollAnswer implements Parcelable {


    private int answerId;
    private String answer;
    private int answerOrder;
    private float respPercent;
    private int noOfVote;


    public int getNoOfVote() {
        return noOfVote;
    }

    public void setNoOfVote(int noOfVote) {
        this.noOfVote = noOfVote;
    }


    public void setRespPercent(float respPercent) {
        this.respPercent = respPercent;
    }


    public PollAnswer() {

    }

    public PollAnswer(String answer) {
        this.answer = answer;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswerOrder(int answerOrder) {
        this.answerOrder = answerOrder;
    }

    public int getAnswerOrder() {
        return answerOrder;
    }

    public float getRespPercent() {
        return respPercent;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PollAnswer) {
            PollAnswer pollAnswer = (PollAnswer) obj;
            return this.answer.equals(pollAnswer.getAnswer());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.answerId);
        dest.writeString(this.answer);
        dest.writeInt(this.answerOrder);
        dest.writeFloat(this.respPercent);
        dest.writeInt(this.noOfVote);
    }

    protected PollAnswer(Parcel in) {
        this.answerId = in.readInt();
        this.answer = in.readString();
        this.answerOrder = in.readInt();
        this.respPercent = in.readFloat();
        this.noOfVote = in.readInt();
    }

    public static final Parcelable.Creator<PollAnswer> CREATOR = new Parcelable.Creator<PollAnswer>() {
        @Override
        public PollAnswer createFromParcel(Parcel source) {
            return new PollAnswer(source);
        }

        @Override
        public PollAnswer[] newArray(int size) {
            return new PollAnswer[size];
        }
    };
}
