package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.util.StringUtils;

@SuppressWarnings("ALL")
public class User implements Parcelable {

    private boolean admin;
    private String city;
    private String country;
    private boolean friend;
    private String fullName;
    private String homeTown;
    private String profileUrl;
    private String state;
    private String userName;
    private int userId;
    private int userReqStatus = -1;
    private int[] roles;
    private int profileUrlType;
    private boolean requestedByMe;
    private int mutualFriendCount;

    public boolean isFlaggedByMe() {
        return flaggedByMe;
    }

    public void setFlaggedByMe(boolean flaggedByMe) {
        this.flaggedByMe = flaggedByMe;
    }

    private boolean flaggedByMe;
    private University universityBaseModel;

    @Override
    public String toString() {
        return getUserName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            return ((User) obj).getUserId() == userId;
        return false;

    }

    @Override
    public int hashCode() {
        return userId;
    }

    public String getName() {
        if (!StringUtils.isNullOrEmpty(userName)) {
            String handle = userName.startsWith("@") ? userName : "@" + userName;
            return handle.replaceAll("\\s", "");
        }
        return userName;
    }

    public boolean isRequestedByMe() {
        return requestedByMe;
    }

    private boolean isSelected;

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getUserReqStatus() {
        return userReqStatus;
    }

    public boolean isAdmin() {
        return admin;
    }

    public int[] getRoles() {
        return roles;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public boolean isFriend() {
        return friend;
    }

    public String getFullName() {
        return fullName;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getState() {
        return state;
    }

    public int getUserId() {
        return userId;
    }

    public int getMutualFriendCount() {
        return mutualFriendCount;
    }

    public String getUserName() {
        return userName;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public int getProfileUrlType() {
        return profileUrlType;
    }

    public University getUniversityBaseModel() {
        return universityBaseModel;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserReqStatus(int userReqStatus) {
        this.userReqStatus = userReqStatus;
    }


    public void setRequestedByMe(boolean requestedByMe) {
        this.requestedByMe = requestedByMe;
    }

    public void setProfileUrlType(int profileUrlType) {
        this.profileUrlType = profileUrlType;
    }

    public void setMutualFriendCount(int mutualFriendCount) {
        this.mutualFriendCount = mutualFriendCount;
    }

    public void setUniversityBaseModel(University universityBaseModel) {
        this.universityBaseModel = universityBaseModel;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeByte(this.friend ? (byte) 1 : (byte) 0);
        dest.writeString(this.fullName);
        dest.writeString(this.homeTown);
        dest.writeString(this.profileUrl);
        dest.writeString(this.state);
        dest.writeString(this.userName);
        dest.writeInt(this.userId);
        dest.writeInt(this.userReqStatus);
        dest.writeIntArray(this.roles);
        dest.writeInt(this.profileUrlType);
        dest.writeByte(this.requestedByMe ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mutualFriendCount);
        dest.writeByte(this.flaggedByMe ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.universityBaseModel, flags);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.admin = in.readByte() != 0;
        this.city = in.readString();
        this.country = in.readString();
        this.friend = in.readByte() != 0;
        this.fullName = in.readString();
        this.homeTown = in.readString();
        this.profileUrl = in.readString();
        this.state = in.readString();
        this.userName = in.readString();
        this.userId = in.readInt();
        this.userReqStatus = in.readInt();
        this.roles = in.createIntArray();
        this.profileUrlType = in.readInt();
        this.requestedByMe = in.readByte() != 0;
        this.mutualFriendCount = in.readInt();
        this.flaggedByMe = in.readByte() != 0;
        this.universityBaseModel = in.readParcelable(University.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
