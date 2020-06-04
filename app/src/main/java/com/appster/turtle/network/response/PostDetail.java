/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.appster.turtle.adapter.viewholder.MeetupViewHolder;
import com.appster.turtle.network.request.PollAnswer;
import com.appster.turtle.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.appster.turtle.util.LogUtils.LOGE;

/**
 * Created by atul on 05/09/17.
 * To inject activity reference.
 */

public class PostDetail implements Parcelable {

    // Fields for Text
    private String text;

    // Fields for Media
    private List<Media> postMediaList;

    // Fields for Poll
    private String question;
    private int totResponse;
    private int userAnswerId;
    private boolean participated;
    private List<PollAnswer> pollAnswersList;

    // Fields for MeetUp
    private double lat;
    private double lon;
    private String title;
    private String address;
    private String duration;
    //    private long scheduledAt;
    private String scheduledAt;
    private String dateFormatted;
    private String meetupData;
    private int userResponse;

    public int getDurationId() {
        return durationId;
    }

    private int durationId;
    private boolean meetupResponded;

    public boolean isChoiceSelected() {
        return isChoiceSelected;
    }

    public void setChoiceSelected(boolean choiceSelected) {
        isChoiceSelected = choiceSelected;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    private boolean isChoiceSelected;
    private boolean isShown;

    public String getText() {
        return text;
    }

    public List<Media> getPostMediaList() {
        return postMediaList;
    }

    public boolean isParticipated() {
        return participated;
    }

    public void setParticipated(boolean participated) {
        this.participated = participated;
    }

    public String getQuestion() {
        return question;
    }

    public int getTotResponse() {
        return totResponse;
    }

    public int getUserAnswerId() {
        return userAnswerId;
    }

    public List<PollAnswer> getPollAnswersList() {
        return pollAnswersList;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getDuration() {
        return duration;
    }

//    public long getScheduledAt() {
//        return scheduledAt;
//    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public String getScheduledTime() {
        String dateTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = dateFormat.parse(scheduledAt);
            return dateFormat.format(date);
        } catch (ParseException e) {
            LogUtils.printStackTrace(e);
        }
        return dateTime;
    }

    public String getDateFormatted() {

        //obtained dateString is of format MM-dd-yyyy HH:mm:ss
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

        //set dateFormat to UTC since obtained dateString is in UTC
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //convert obtained dateString to Date
        Date convertedUtcDate;
        try {
            convertedUtcDate = dateFormat.parse(scheduledAt);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing scheduledAt: " + scheduledAt);
            return "";
        }

        //set dateFormat to local
        dateFormat.setTimeZone(TimeZone.getDefault());

        //convert UTC dateString to local dateString
        String localDateString = dateFormat.format(convertedUtcDate);
        Date localDate;
        try {
            localDate = dateFormat.parse(localDateString);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing localDateString: " + localDateString);
            return "";
        }

        String dayOfMonth = (String) DateFormat.format("dd", localDate); // 20
        String month = (String) DateFormat.format("MMMM", localDate); // Jun
        String year = (String) DateFormat.format("yyyy", localDate); // 2013
        String time = (String) DateFormat.format("h:mm aa", localDate);

        return "at " + time.toLowerCase() + " on " + dayOfMonth + " " + month + ", " + year;
    }


    public String getDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

        //set dateFormat to UTC since obtained dateString is in UTC
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //convert obtained dateString to Date
        Date convertedUtcDate;
        try {
            convertedUtcDate = dateFormat.parse(scheduledAt);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing scheduledAt: " + scheduledAt);
            return "";
        }

        //set dateFormat to local
        dateFormat.setTimeZone(TimeZone.getDefault());

        //convert UTC dateString to local dateString
        String localDateString = dateFormat.format(convertedUtcDate);
        Date localDate;
        try {
            localDate = dateFormat.parse(localDateString);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing localDateString: " + localDateString);
            return "";
        }

        String dayOfMonth = (String) DateFormat.format("dd", localDate); // 20
        String month = (String) DateFormat.format("MMMM", localDate); // Jun
        String year = (String) DateFormat.format("yyyy", localDate); // 2013
        String time = (String) DateFormat.format("h:mm aa", localDate);

        return dayOfMonth + " " + month + ", " + year;
    }

    public String getTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

        //set dateFormat to UTC since obtained dateString is in UTC
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //convert obtained dateString to Date
        Date convertedUtcDate;
        try {
            convertedUtcDate = dateFormat.parse(scheduledAt);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing scheduledAt: " + scheduledAt);
            return "";
        }

        //set dateFormat to local
        dateFormat.setTimeZone(TimeZone.getDefault());

        //convert UTC dateString to local dateString
        String localDateString = dateFormat.format(convertedUtcDate);
        Date localDate;
        try {
            localDate = dateFormat.parse(localDateString);
        } catch (ParseException e) {
            LOGE(MeetupViewHolder.class.getName(), "Error parsing localDateString: " + localDateString);
            return "";
        }

//        String dayOfMonth = (String) DateFormat.format("dd", localDate); // 20
//        String month = (String) DateFormat.format("MMMM", localDate); // Jun
//        String year = (String) DateFormat.format("yyyy", localDate); // 2013
        String time = (String) DateFormat.format("h:mm aa", localDate);

        return time;
    }


    public String getDataString() {

        return "at " + getTime() + " on " + getDate();
    }


    public String getMeetupData() {
        if (address.isEmpty()) {
            return getDataString() + " for " + duration;
        } else
            return getDataString() + " for " + duration + " at " + address;
    }

    public int getUserResponse() {
        return userResponse;
    }

    public boolean isMeetupResponded() {
        return meetupResponded;
    }

    public PostDetail() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeTypedList(this.postMediaList);
        dest.writeString(this.question);
        dest.writeInt(this.totResponse);
        dest.writeInt(this.userAnswerId);
        dest.writeByte(this.participated ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.pollAnswersList);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeString(this.duration);
        dest.writeString(this.scheduledAt);
        dest.writeString(this.dateFormatted);
        dest.writeString(this.meetupData);
        dest.writeInt(this.userResponse);
        dest.writeInt(this.durationId);
        dest.writeByte(this.meetupResponded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isChoiceSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShown ? (byte) 1 : (byte) 0);
    }

    protected PostDetail(Parcel in) {
        this.text = in.readString();
        this.postMediaList = in.createTypedArrayList(Media.CREATOR);
        this.question = in.readString();
        this.totResponse = in.readInt();
        this.userAnswerId = in.readInt();
        this.participated = in.readByte() != 0;
        this.pollAnswersList = in.createTypedArrayList(PollAnswer.CREATOR);
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.title = in.readString();
        this.address = in.readString();
        this.duration = in.readString();
        this.scheduledAt = in.readString();
        this.dateFormatted = in.readString();
        this.meetupData = in.readString();
        this.userResponse = in.readInt();
        this.durationId = in.readInt();
        this.meetupResponded = in.readByte() != 0;
        this.isChoiceSelected = in.readByte() != 0;
        this.isShown = in.readByte() != 0;
    }

    public static final Creator<PostDetail> CREATOR = new Creator<PostDetail>() {
        @Override
        public PostDetail createFromParcel(Parcel source) {
            return new PostDetail(source);
        }

        @Override
        public PostDetail[] newArray(int size) {
            return new PostDetail[size];
        }
    };
}