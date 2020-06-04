package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class UserBaseModel implements Parcelable {


    private int userId;
    private String name;
    private String fName;
    private String lName;
    private String userName;
    private String email;
    private String bio;
    private String profileStatus;
    private String homeTown;
    private float age;
    private String gender;
    private int[] roles;
    private ArrayList<Integer> interestIds;
    private ArrayList<Integer> skillIds;
    private String profileUrl;
    private boolean profileResetReq;
    private int profileUrlType;
    private boolean emailVerified;
    private boolean profileComplete;
    private boolean userBlockedInRoom;
    private boolean isPublicProfile;

    public boolean isPasswordResetReq() {
        return passwordResetReq;
    }

    public void setPasswordResetReq(boolean passwordResetReq) {
        this.passwordResetReq = passwordResetReq;
    }

    private boolean passwordResetReq;

    private Dorm dormitory;
    private ArrayList<GreekLife> greekLifeAffiliation = new ArrayList<>();
    private ArrayList<MajorModel> academicDisciplines = new ArrayList<>();
    private GraduationYear academicYear;


    private ArrayList<Interest> interests = new ArrayList<>();
    private ArrayList<Interest> skills = new ArrayList<>();

    private ArrayList<Integer> major = new ArrayList<>();
    private ArrayList<Integer> minor = new ArrayList<>();
    private ArrayList<Integer> sororities = new ArrayList<>();
    private ArrayList<Integer> fraternities = new ArrayList<>();
    private int academicYearId;
    private int dormatoryId;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Integer> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(ArrayList<Integer> skillIds) {
        this.skillIds = skillIds;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return fName + " " + lName;
    }


    public String getfName() {
        return fName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUsernameWithAtSign() {
        return "@" + userName;
    }

    public String getEmail() {
        if (email == null) {
            email = "";
        }
        return email;
    }


    public String getProfileStatus() {
        return profileStatus;
    }

    public float getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public int[] getRoles() {
        return roles;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public boolean isProfileResetReq() {
        return profileResetReq;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;

    }

    public ArrayList<Integer> getInterestIds() {
        return interestIds;
    }

    public void setInterestIds(ArrayList<Integer> interestIds) {
        this.interestIds = interestIds;
    }

    public boolean isUserBlockedInRoom() {
        return userBlockedInRoom;
    }

    public void setUserBlockedInRoom(boolean userBlockedInRoom) {
        this.userBlockedInRoom = userBlockedInRoom;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setProfileUrlType(int profileUrlType) {
        this.profileUrlType = profileUrlType;
    }

    public String getBio() {
        return StringUtils.isNullOrEmpty(bio) ? "" : bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public int getProfileUrlType() {
        return profileUrlType;
    }

    public void setInterests(ArrayList<Interest> interests) {
        this.interests = interests;
    }

    public void setSkills(ArrayList<Interest> skills) {
        this.skills = skills;
    }

    public ArrayList<Interest> getInterests() {
        return interests;
    }

    public String getInterestStr() {

        if (interests.isEmpty())
            return "";
        else {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < interests.size(); i++) {
                string.append("," + interests.get(i).getValue().toString());
            }
            return string.toString().replaceFirst(",", "");

        }
    }

    public ArrayList<MajorModel> getAcademicDisciplines() {
        return academicDisciplines;
    }

    public void setAcademicDisciplines(ArrayList<MajorModel> academicDisciplines) {
        this.academicDisciplines = academicDisciplines;
    }

    public String getMajorStr() {
        if (academicDisciplines != null && !academicDisciplines.isEmpty()) {
            StringBuilder string = new StringBuilder();
            for (MajorModel m : academicDisciplines) {

                if (m.getType().equalsIgnoreCase("major")) {
                    string.append("," + m.getValue().toString());
                }

            }

            return string.toString().replaceFirst(",", "");
        }
        return "";
    }


    public String getMinorStr() {
        if (academicDisciplines != null && !academicDisciplines.isEmpty()) {
            StringBuilder string = new StringBuilder();
            for (MajorModel m : academicDisciplines) {

                if (m.getType().equalsIgnoreCase("minor")) {
                    string.append("," + m.getValue().toString());
                }

            }

            return string.toString().replaceFirst(",", "");
        }
        return "";
    }


    public ArrayList<Interest> getSkills() {
        return skills;
    }

    public String getSkillStr() {

        if (skills.isEmpty())
            return "";
        else {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < skills.size(); i++) {
                string.append("," + skills.get(i).getValue().toString());
            }
            return string.toString().replaceFirst(",", "");

        }

    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public ArrayList<Integer> getMajor() {
        return major;
    }

    public void setMajor(ArrayList<Integer> major) {
        this.major = major;
    }

    public ArrayList<Integer> getMinor() {
        return minor;
    }

    public void setMinor(ArrayList<Integer> minor) {
        this.minor = minor;
    }

    public ArrayList<Integer> getSororities() {
        return sororities;
    }

    public void setSororities(ArrayList<Integer> sororities) {
        this.sororities = sororities;
    }

    public ArrayList<Integer> getFraternities() {
        return fraternities;
    }

    public void setFraternities(ArrayList<Integer> fraternities) {
        this.fraternities = fraternities;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(int academicYearId) {
        this.academicYearId = academicYearId;
    }

    public void setAcademicYear(GraduationYear academicYear) {
        this.academicYear = academicYear;
    }

    public int getDormatoryId() {
        return dormatoryId;
    }

    public void setDormatoryId(int dormatoryId) {
        this.dormatoryId = dormatoryId;
    }

    public void setDormitory(Dorm dormitory) {
        this.dormitory = dormitory;
    }

    public boolean isPublicProfile() {
        return isPublicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        isPublicProfile = publicProfile;
    }

    public Dorm getDormitory() {
        return dormitory;
    }

    public String getDormitoryStr() {
        return dormitory != null ? dormitory.getTitle() : "";
    }

    public ArrayList<GreekLife> getGreekLifeAffiliation() {
        return greekLifeAffiliation;
    }

    public String getGreekLivesStr() {

        if (greekLifeAffiliation.isEmpty())
            return "";
        else {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < greekLifeAffiliation.size(); i++) {
                string.append("," + greekLifeAffiliation.get(i).getValue().toString());
            }
            return string.toString().replaceFirst(",", "");

        }
    }

    public String getlName() {
        return lName;
    }

    public String getAcademicYearStr() {

        if (academicYear != null) {
            return academicYear.getValue();
        }
        return "";

    }

    public GraduationYear getAcademicYear() {
        return academicYear;

    }

    public ArrayList<GreekLife> getGreekLife(boolean isFraternities) {

        ArrayList<GreekLife> greekLives = new ArrayList<>();
        for (GreekLife greeklife : greekLifeAffiliation) {

            if (greeklife.getType().equalsIgnoreCase(isFraternities ? Constants.FRATERNITIES : Constants.SORORITIES)) {
                greekLives.add(greeklife);
            }
        }

        return greekLives;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MajorModel> getMajorMinor(boolean isMajor) {

        ArrayList<MajorModel> majorModels = new ArrayList<>();
        for (MajorModel majorModel : academicDisciplines) {

            if (majorModel.getType().equalsIgnoreCase(isMajor ? Constants.MAJOR : Constants.MINOR)) {
                majorModels.add(majorModel);
            }
        }

        return majorModels;
    }

    public UserBaseModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.fName);
        dest.writeString(this.lName);
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeString(this.bio);
        dest.writeString(this.profileStatus);
        dest.writeString(this.homeTown);
        dest.writeFloat(this.age);
        dest.writeString(this.gender);
        dest.writeIntArray(this.roles);
        dest.writeList(this.interestIds);
        dest.writeList(this.skillIds);
        dest.writeString(this.profileUrl);
        dest.writeByte(this.profileResetReq ? (byte) 1 : (byte) 0);
        dest.writeInt(this.profileUrlType);
        dest.writeByte(this.emailVerified ? (byte) 1 : (byte) 0);
        dest.writeByte(this.profileComplete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userBlockedInRoom ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPublicProfile ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.dormitory, flags);
        dest.writeTypedList(this.greekLifeAffiliation);
        dest.writeTypedList(this.academicDisciplines);
        dest.writeParcelable(this.academicYear, flags);
        dest.writeTypedList(this.interests);
        dest.writeTypedList(this.skills);
        dest.writeList(this.major);
        dest.writeList(this.minor);
        dest.writeList(this.sororities);
        dest.writeList(this.fraternities);
        dest.writeInt(this.academicYearId);
        dest.writeInt(this.dormatoryId);
    }

    protected UserBaseModel(Parcel in) {
        this.userId = in.readInt();
        this.name = in.readString();
        this.fName = in.readString();
        this.lName = in.readString();
        this.userName = in.readString();
        this.email = in.readString();
        this.bio = in.readString();
        this.profileStatus = in.readString();
        this.homeTown = in.readString();
        this.age = in.readFloat();
        this.gender = in.readString();
        this.roles = in.createIntArray();
        this.interestIds = new ArrayList<Integer>();
        in.readList(this.interestIds, Integer.class.getClassLoader());
        this.skillIds = new ArrayList<Integer>();
        in.readList(this.skillIds, Integer.class.getClassLoader());
        this.profileUrl = in.readString();
        this.profileResetReq = in.readByte() != 0;
        this.profileUrlType = in.readInt();
        this.emailVerified = in.readByte() != 0;
        this.profileComplete = in.readByte() != 0;
        this.userBlockedInRoom = in.readByte() != 0;
        this.isPublicProfile = in.readByte() != 0;
        this.dormitory = in.readParcelable(Dorm.class.getClassLoader());
        this.greekLifeAffiliation = in.createTypedArrayList(GreekLife.CREATOR);
        this.academicDisciplines = in.createTypedArrayList(MajorModel.CREATOR);
        this.academicYear = in.readParcelable(GraduationYear.class.getClassLoader());
        this.interests = in.createTypedArrayList(Interest.CREATOR);
        this.skills = in.createTypedArrayList(Interest.CREATOR);
        this.major = new ArrayList<Integer>();
        in.readList(this.major, Integer.class.getClassLoader());
        this.minor = new ArrayList<Integer>();
        in.readList(this.minor, Integer.class.getClassLoader());
        this.sororities = new ArrayList<Integer>();
        in.readList(this.sororities, Integer.class.getClassLoader());
        this.fraternities = new ArrayList<Integer>();
        in.readList(this.fraternities, Integer.class.getClassLoader());
        this.academicYearId = in.readInt();
        this.dormatoryId = in.readInt();
    }

    public static final Parcelable.Creator<UserBaseModel> CREATOR = new Parcelable.Creator<UserBaseModel>() {
        @Override
        public UserBaseModel createFromParcel(Parcel source) {
            return new UserBaseModel(source);
        }

        @Override
        public UserBaseModel[] newArray(int size) {
            return new UserBaseModel[size];
        }
    };
}
