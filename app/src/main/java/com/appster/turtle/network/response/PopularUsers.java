package com.appster.turtle.network.response;

/**
 * Created  on 19/02/18 .
 */

public class PopularUsers {

    private boolean admin;
    //    private boolean blocked;
    private String city;
    private String country;
    private String cropProfileUrl;
    private boolean friend;
    private String fullName;

    private String homeTown;
    private boolean isPublicProfile;
    private String profileUrl;
    private int profileUrlType;

    private String state;
    private UniversityBaseModel universityBaseModel;
    private int userId;
    private String userName;
    private int userReqStatus;


    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

//    public boolean isBlocked() {
//        return blocked;
//    }
//
//    public void setBlocked(boolean blocked) {
//        this.blocked = blocked;
//    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCropProfileUrl() {
        return cropProfileUrl;
    }

    public void setCropProfileUrl(String cropProfileUrl) {
        this.cropProfileUrl = cropProfileUrl;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public boolean isPublicProfile() {
        return isPublicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        isPublicProfile = publicProfile;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getProfileUrlType() {
        return profileUrlType;
    }

    public void setProfileUrlType(int profileUrlType) {
        this.profileUrlType = profileUrlType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UniversityBaseModel getUniversityBaseModel() {
        return universityBaseModel;
    }

    public void setUniversityBaseModel(UniversityBaseModel universityBaseModel) {
        this.universityBaseModel = universityBaseModel;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserReqStatus() {
        return userReqStatus;
    }

    public void setUserReqStatus(int userReqStatus) {
        this.userReqStatus = userReqStatus;
    }

}
