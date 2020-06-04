/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Attachment implements Parcelable {

    private String fileName;
    private String fileUrl;
    private Integer id;
    private Integer notesId;
    private File file;
    private Integer uploadType;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNotesId(Integer notesId) {
        this.notesId = notesId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNotesId() {
        return notesId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.fileUrl);
        dest.writeValue(this.id);
        dest.writeValue(this.notesId);
        dest.writeValue(this.file);
        dest.writeValue(this.uploadType);
    }

    public Attachment() {
    }

    protected Attachment(Parcel in) {
        this.fileName = in.readString();
        this.fileUrl = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.notesId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.file = (File) in.readValue(Integer.class.getClassLoader());
        this.uploadType = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel source) {
            return new Attachment(source);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    @Override
    public String toString() {
        return this.id + "";
    }
}
