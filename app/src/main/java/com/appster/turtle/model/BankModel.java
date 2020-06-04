package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("ALL")
public class BankModel implements Parcelable {

    private String accountNumber;
    private String address;
    private String dob;
    private String firstName;
    private String lastName;
    private String postcode;
    private String routingNumber;
    private String ssn;
    private String state;
    private String suburb;
    private String verificationStatus;


    public String getVerificationStatus() {
        return verificationStatus;
    }

    private String documentUrl;
    private String verificationDocumentId;
    private boolean isFromServer;


    public boolean isFromServer() {
        return isFromServer;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }


    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getVerificationDocumentId() {
        return verificationDocumentId;
    }

    public void setVerificationDocumentId(String verificationDocumentId) {
        this.verificationDocumentId = verificationDocumentId;
    }

    public BankModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountNumber);
        dest.writeString(this.address);
        dest.writeString(this.dob);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.postcode);
        dest.writeString(this.routingNumber);
        dest.writeString(this.ssn);
        dest.writeString(this.state);
        dest.writeString(this.suburb);
        dest.writeString(this.verificationStatus);
        dest.writeString(this.documentUrl);
        dest.writeString(this.verificationDocumentId);
        dest.writeByte(this.isFromServer ? (byte) 1 : (byte) 0);
    }

    protected BankModel(Parcel in) {
        this.accountNumber = in.readString();
        this.address = in.readString();
        this.dob = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.postcode = in.readString();
        this.routingNumber = in.readString();
        this.ssn = in.readString();
        this.state = in.readString();
        this.suburb = in.readString();
        this.verificationStatus = in.readString();
        this.documentUrl = in.readString();
        this.verificationDocumentId = in.readString();
        this.isFromServer = in.readByte() != 0;
    }

    public static final Creator<BankModel> CREATOR = new Creator<BankModel>() {
        @Override
        public BankModel createFromParcel(Parcel source) {
            return new BankModel(source);
        }

        @Override
        public BankModel[] newArray(int size) {
            return new BankModel[size];
        }
    };
}
