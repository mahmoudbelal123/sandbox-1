package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 */

public class University implements Parcelable {

    private String id;
    private String studentCount;
    private String emailExtension;
    private String status;
    private String name;
    private String logoUrl;
    private String displayName;

    public University(String id,
                      String studentCount,
                      String emailExtension,
                      String status,
                      String name,
                      String logoUrl,
                      String displayName) {
        this.id = id;
        this.studentCount = studentCount;
        this.emailExtension = emailExtension;
        this.status = status;
        this.name = name;
        this.logoUrl = logoUrl;
        this.displayName = displayName;


    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentCount(String studentCount) {
        this.studentCount = studentCount;
    }

    public void setEmailExtension(String emailExtension) {
        this.emailExtension = emailExtension;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected University(Parcel in) {
        id = in.readString();
        studentCount = in.readString();
        emailExtension = in.readString();
        status = in.readString();
        name = in.readString();
        logoUrl = in.readString();
        displayName = in.readString();
    }

    public static final Creator<University> CREATOR = new Creator<University>() {
        @Override
        public University createFromParcel(Parcel in) {
            return new University(in);
        }

        @Override
        public University[] newArray(int size) {
            return new University[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getStudentCount() {
        return studentCount;
    }

    public String getEmailExtension() {
        return emailExtension;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(studentCount);
        parcel.writeString(emailExtension);
        parcel.writeString(status);
        parcel.writeString(name);
        parcel.writeString(logoUrl);
        parcel.writeString(displayName);
    }
}
