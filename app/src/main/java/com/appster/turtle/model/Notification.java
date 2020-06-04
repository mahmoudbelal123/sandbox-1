package com.appster.turtle.model;

import java.util.List;

/**
 * Created  on 10/10/17 .
 */

public class Notification {

    private String id;
    private String modifiedAt;
    private List<MediaMeta> mediaMeta;
    private String createdAt;
    private int notificationType;
    private List<MessageMeta> messageMeta;
    private Data data;

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    private String isRead;

    public String getId() {
        return id;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public List<MediaMeta> getMediaMeta() {
        return mediaMeta;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public List<MessageMeta> getMessageMeta() {
        return messageMeta;
    }

    public Data getData() {
        return data;
    }

    public String getIsRead() {
        return isRead;
    }

    public class Data {
        private String roomId;
        private int senderId;
        private String postId;

        public int getCommentId() {
            return commentId;
        }

        public int getNotificationType() {
            return notificationType;
        }

        public int getNoteId() {
            return noteId;
        }

        private int commentId;
        private int notificationType;
        private int noteId;

        public String getRoomId() {
            return roomId;
        }

        public int getSenderIds() {
            return senderId;
        }

        public String getPostId() {
            return postId;
        }

    }
}
