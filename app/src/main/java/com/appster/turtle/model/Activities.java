package com.appster.turtle.model;

import java.util.ArrayList;

public class Activities {

    private int activityType;
    private Data data;
    private ArrayList<MessageMeta> messageMeta;
    private long createdAt;
    private long modifiedAt;
    private boolean isDate = false;

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<MessageMeta> getMessageMeta() {
        return messageMeta;
    }

    public int getActivityType() {
        return activityType;
    }

    public Data getData() {
        return data;
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

    public class MessageMeta {

        private String type;
        private String message;
        private ArrayList<Integer> data;

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public ArrayList<Integer> getData() {
            return data;
        }
    }
}
