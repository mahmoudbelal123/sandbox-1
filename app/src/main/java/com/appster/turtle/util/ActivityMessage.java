/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

public class ActivityMessage {

    public interface ActivityType {
        int CREATE_ROOM = 1;
        int POST_TEXT = 2;
        int POST_PICTURE = 3;
        int POST_VIDEO = 4;
        int POST_GIF = 5;
        int POST_POLL = 6;
        int POST_MEETUP = 7;
        int POST_TEXT_COMMENT = 8;
        int POST_PICTURE_COMMENT = 9;
        int POST_VIDEO_COMMENT = 10;
        int POST_GIF_COMMENT = 11;
        int POST_POLL_COMMENT = 12;
        int POST_MEETUP_COMMENT = 13;
        int POST_TEXT_LIKE = 14;
        int POST_PICTURE_LIKE = 15;
        int POST_VIDEO_LIKE = 16;
        int POST_GIF_LIKE = 17;
        int POST_POLL_LIKE = 18;
        int POST_MEETUP_LIKE = 19;
        int POST_TEXT_COMMENT_LIKE = 20;
        int POST_PICTURE_COMMENT_LIKE = 21;
        int POST_VIDEO_COMMENT_LIKE = 22;
        int POST_GIF_COMMENT_LIKE = 23;
        int POST_POLL_COMMENT_LIKE = 24;
        int POST_MEETUP_COMMENT_LIKE = 25;
        int MAKE_ROOM_ADMIN = 26;
        int JOIN_ROOM = 27;
        int ACCEPT_FRIEND_REQUEST = 28;
        int POST_MEETUP_RESPONSE = 29;
    }

    public interface MessageType {
        String PROFILE = "PROFILE";
        String GROUP_PROFILE = "GROUP_PROFILE";
        String NORMAL = "NORMAL";
        String ROOM = "ROOM";
        String MEDIA = "MEDIA";
        String MEETUP = "MEETUP";
        String ABOUT_TXT = "ABOUT_TXT";
        String STATUS = "STATUS";
    }

    public interface MediaType {
        String PICTURE_THUMB = "PICTURE_THUMB";
    }

    public interface NotificationType {

        int FRIEND_REQUEST = 1;
        int POST_TEXT_COMMENT = 2;
        int POST_PICTURE_COMMENT = 3;
        int POST_VIDEO_COMMENT = 4;
        int POST_GIF_COMMENT = 5;
        int POST_POLL_COMMENT = 6;
        int POST_MEETUP_COMMENT = 7;
        int POST_TEXT_LIKE = 8;
        int POST_PICTURE_LIKE = 9;
        int POST_VIDEO_LIKE = 10;
        int POST_GIF_LIKE = 11;
        int POST_MEETUP_LIKE = 12;
        int POST_POLL_LIKE = 13;
        int POST_TEXT_COMMENT_LIKE = 14;
        int POST_PICTURE_COMMENT_LIKE = 15;
        int POST_VIDEO_COMMENT_LIKE = 16;
        int POST_GIF_COMMENT_LIKE = 17;
        int POST_POLL_COMMENT_LIKE = 18;
        int POST_MEETUP_COMMENT_LIKE = 19;
        int POST_TEXT_TAG = 21;
        int ROOM_REQUEST = 20;
        int NOTE_PURCHASED_EVENT = 22;
        int NOTE_REVIEW_EVENT = 23;
        int POST_MEETUP_RESPONSE_ATTENDING = 24;
        int POST_MEETUP_RESPONSE_INTERESTED = 25;
        int POST_POLL_RESPONSE = 26;
        int POST_MEETUP_EDIT = 27;
        int COMMENT_TAG = 28;
        int ROOM_JOIN_REQUEST = 29;
        int BECOME_ROOM_ADMIN = 30;
        int POST_USER_TAG = 31;
        int POST_USER_TAG_MEDIA = 32;


    }


}
