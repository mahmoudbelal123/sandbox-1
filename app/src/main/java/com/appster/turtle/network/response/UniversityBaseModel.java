package com.appster.turtle.network.response;

/**
 * Created  on 19/02/18 .
 */

public class UniversityBaseModel {

    private String displayName;
    private String emailExtension;
    private int id;
    private String logoUrl;
    private String name;
    private String status;
    private int studentCount;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailExtension() {
        return emailExtension;
    }

    public void setEmailExtension(String emailExtension) {
        this.emailExtension = emailExtension;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }


}
