package com.appster.turtle.model;

import android.databinding.Observable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NotesModel implements Parcelable, Observable {

    private Boolean active;
    private List<Attachment> attachments = null;
    private Float avgRating;
    private String className;
    private String details;
    private Integer id;
    private String postedBy;
    private Boolean postedByMe;
    private Double price;
    private Boolean purchasedByMe;
    private Integer reviewCount;
    private Boolean reviewedByMe;
    private String snippetUrl;
    private String title;

    public User getUsersListModel() {
        return usersListModel;
    }

    public void setUsersListModel(User usersListModel) {
        this.usersListModel = usersListModel;
    }

    private User usersListModel;

    public NotesModel() {
    }

    public Boolean getActive() {
        return active;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public String getClassName() {
        return className;
    }

    public String getDetails() {
        return details;
    }

    public Integer getId() {
        return id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public Boolean getPostedByMe() {
        return postedByMe;
    }

    public Double getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        String notesPriceString = String.valueOf(price);

        if (notesPriceString.endsWith(".0") || notesPriceString.endsWith(".00")) {
            return notesPriceString.replaceAll("\\.0*$", "");
        }

        return notesPriceString;
    }

    public Boolean getPurchasedByMe() {
        return purchasedByMe;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public Boolean getReviewedByMe() {
        return reviewedByMe;
    }

    public String getSnippetUrl() {
        return snippetUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public void setPostedByMe(Boolean postedByMe) {
        this.postedByMe = postedByMe;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPurchasedByMe(Boolean purchasedByMe) {
        this.purchasedByMe = purchasedByMe;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setReviewedByMe(Boolean reviewedByMe) {
        this.reviewedByMe = reviewedByMe;
    }

    public void setSnippetUrl(String snippetUrl) {
        this.snippetUrl = snippetUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.active);
        dest.writeTypedList(this.attachments);
        dest.writeValue(this.avgRating);
        dest.writeString(this.className);
        dest.writeString(this.details);
        dest.writeValue(this.id);
        dest.writeString(this.postedBy);
        dest.writeValue(this.postedByMe);
        dest.writeValue(this.price);
        dest.writeValue(this.purchasedByMe);
        dest.writeValue(this.reviewCount);
        dest.writeValue(this.reviewedByMe);
        dest.writeString(this.snippetUrl);
        dest.writeString(this.title);
        dest.writeParcelable(this.usersListModel, flags);
    }

    protected NotesModel(Parcel in) {
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.attachments = in.createTypedArrayList(Attachment.CREATOR);
        this.avgRating = (Float) in.readValue(Float.class.getClassLoader());
        this.className = in.readString();
        this.details = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.postedBy = in.readString();
        this.postedByMe = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.purchasedByMe = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.reviewCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.reviewedByMe = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.snippetUrl = in.readString();
        this.title = in.readString();
        this.usersListModel = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<NotesModel> CREATOR = new Creator<NotesModel>() {
        @Override
        public NotesModel createFromParcel(Parcel source) {
            return new NotesModel(source);
        }

        @Override
        public NotesModel[] newArray(int size) {
            return new NotesModel[size];
        }
    };
}
