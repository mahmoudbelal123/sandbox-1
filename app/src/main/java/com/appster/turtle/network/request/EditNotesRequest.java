/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.request;

import com.appster.turtle.model.Attachment;

import java.util.List;

/**
 * Created  on 18/10/17 .
 */

public class EditNotesRequest {

    private List<Integer> remAttachmentIds;
    private List<Attachment> newAttachments;
    private Integer id;
    private String title;
    private String details;
    private String className;
    private VerificationImage snippet;

    public EditNotesRequest() {

    }

    public List<Integer> getRemAttachmentIds() {
        return remAttachmentIds;
    }

    public List<Attachment> getNewAttachments() {
        return newAttachments;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getClassName() {
        return className;
    }

    public VerificationImage getSnippet() {
        return snippet;
    }

    public void setRemAttachmentIds(List<Integer> remAttachmentIds) {
        this.remAttachmentIds = remAttachmentIds;
    }

    public void setNewAttachments(List<Attachment> newAttachments) {
        this.newAttachments = newAttachments;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSnippet(VerificationImage snippet) {
        this.snippet = snippet;
    }
}
