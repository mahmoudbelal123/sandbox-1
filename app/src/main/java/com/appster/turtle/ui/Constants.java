/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui;

import android.os.Environment;

import java.io.File;

/**
 * Constant file
 */
public class Constants {

    public static final String ROOM_NAME_KEY = "room_name";
    public static final String ROOM_DESC_KEY = "room_desc";
    public static final String USER_IDS_KEY = "user_ids";
    public static final String IS_EDIT = "isEdit";
    public static final String KEY = "key";
    public static final int MAX_CHARACTERS_TEXT_POST = 512;
    public static final Double NOTES_PRICE_LIMIT = 999.99;
    public static final int MAX_CHARACTERS_ADD_DESC = 500;

    public static final int PLACES_SELECTOR_REQUEST_CODE = 100;
    public static final int FORGOT_PASS_REQUEST = 1029;
    public static final File CROPPED_IMAGE_URI = new File(
            Environment.getExternalStorageDirectory(),
            "crop_file.jpg");

    public static final String ADMINS_LIST = "ADMINS_LIST";
    public static final String HEADER_TITLE = "HEADER_TITLE";
    public static final String POS = "pos";
    public static final String FILTER = "filter";
    public static final String IS_NEWADD = "isnewadded";
    public static final String IS_ALL_CONNECTION = "IS_ALL_CONNECTION";
    public static final String IS_DELETED = "IS_deleted";


    public static final String POST_QUERY = "post";
    public static final String POST_ID = "POST_ID";
    public static final String USER_ID = "user_id";
    public static final String USER = "user";
    public static final String MAJOR = "major";
    public static final String MINOR = "minor";
    public static final String SELECTED_IDS = "SELECTED_IDS";
    public static final String CHOOSE_AVATAR_USER = "CHOOSE_AVATAR_USER";
    public static final String USER_GLOBAL = "global";
    public static final String USER_CAMPUS = "campus";


    private Constants() {
    }


    public interface RoomRequest {
        int ROOM_REQUEST_ACCEPT = 0;
        int ROOM_REQUEST_DECLINE = 1;
    }

    public interface MyFriend {

        int ALL_REQUESTS_AND_FRIENDS = 1;
        int ACCEPTED_FRIEND = 0;

        int SEND_REQUEST = 1;
        int ACCEPT_REQUEST = 2;
        int REJECT_REQUEST = 3;

        int FRIEND_STATUS_CONNECTED = 1;
        int FRIEND_STATUS_NOT_CONNECTED = -1;
        int FRIEND_STATUS_PENDING = 0;
        int STATUS_REJECTED = 2;

    }


    public interface RoomMember {


        int STATUS_CONNECTED = 1;
        int STATUS_REJECTED = 2;
        int STATUS_NOT_CONNECTED = -1;
        int STATUS_PENDING = 0;
    }

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 15;
    public static final int USER_NAME_MIN_LENGTH = 6;
    public static final int FIRST_NAME_MIN_LENGTH = 2;
    public static final int IMAGE_INITIALS_ID = 3;
    public static final int LIST_LENGTH = 10;
    public static final String TAGS = "TAGS";
    public static final String USER_TAGS = "USER_TAGS";
    public static final String CAPTION = "CAPTION";

    public static final int VIDEO_LENGTH = 180000;
    public static float videoHeight = 0;//NOSONAR
    public static float videoWidth = 0;//NOSONAR

    public static final String FRATERNITIES = "FRATERNITIES";
    public static final String SORORITIES = "SORORITIES";

    public static final String NOTES = "notes";


    public enum USER_MEETUP_RESPONSE {
        ATTENDING(0),
        NOT_ATTENDING(1),
        INTERESTED(2),
        RESET(3);

        private final int value;

        USER_MEETUP_RESPONSE(int value) {
            this.value = value;
        }

        int value() {
            return value;
        }

        public static USER_MEETUP_RESPONSE fromValue(int value) {
            USER_MEETUP_RESPONSE[] list = USER_MEETUP_RESPONSE.values();
            for (USER_MEETUP_RESPONSE action : list) {
                if (action.value() == value) {
                    return action;
                }
            }
            return null;
        }

    }

    public interface eventI {

        int ON_CAMERA_CLICK = 1;
        int ON_IMAGE_CLICK = 2;

    }


    public enum ACTIVITIES {
        SWITCH_ACTIVITY
    }

    public enum FRAGMENTS {
        TEST_FRAGMENT,
        HOME_FRAGMENT,
        MEDIA_BOTTOM_FRAGMENT
    }

    public interface HOME_CURRENT_SCREEN {
        int HOT = 1;
        int NEW = 2;
        int TOP = 3;
    }

    public enum MEDIA_TYPE {
        IMAGE(0),
        VIDEO(1),
        GIF(2),
        PDF(3);


        private final int value;

        MEDIA_TYPE(int value) {
            this.value = value;
        }

        int value() {
            return value;
        }

        public static MEDIA_TYPE fromValue(int value) {
            MEDIA_TYPE[] list = MEDIA_TYPE.values();
            for (MEDIA_TYPE action : list) {
                if (action.value() == value) {
                    return action;
                }
            }
            return null;
        }
    }

    public interface BundleKey {
        String ROOM_TYPE = "room_type";
        String FROM_EDIT_ROOM = "from_Edit_room";
        String LAYOUT_ID = "layoutResId";
        String INDEX = "INDEX";
        String ROOM = "room";
        String ROOM_REFRESH = "room_refresh";
        String ROOM_ID = "roomId";
        String NOTE_ID = "noteId";
        String ROOM_FROM_USER = "ROOM_FROM_USER";
        String FROM_NOTIFICATION = "from_notification";
        String ROOM_NAME = "roomName";
        String IMAGE_TEXT = "image_text";
        String IMAGE_TEXT_POS = "IMAGE_TEXT_POS";
        String IMAGE_URL = "IMAGE_URL";
        String IMAGE_CROPPER_IS_SQUARE = "IMAGE_CROPPER_SHAPE";
        String VIDEO_URL = "VIDEO_URL";
        String POST = "POST";
        String POST_TYPE = "post_type";
        String DATA = "bean";
        String BGR_CLR_ID = "bci";
        String TXT_RES_ID = "ttr";
        String TXT_ID = "text_id";
        String TXT_SIZE_CHANGE = "size_change";
        String IMG_RES_ID = "itr";
        String AUTH_ERR = "auth_err";
        String CARD = "CARD";

        String EDIT_NOTES_REMOVED_ATTACHMENTS = "EDIT_NOTES_REMOVED_ATTACHMENTS";

        String NOTES = "notes";
        String SHOW_TOUR = "bool-extra-show-tour";
        String BANK_MODEL = "bank_model";
        String FROM_SETTINGS = "from_settings";
        String FOR_EDIT = "for-edit";
        String MEETUP = "meetup";
        String PAYMENT_CARD = "PAYMENT_CARD";
        String PAYMENT_CARD_ID = "PAYMENT_CARD_ID";
        String PAYMENT_CUSTOMER_ID = "PAYMENT_CUSTOMER_ID";
        String NEW_ROOM = "new_room";
        String COMMENT = "COMMENT";
        String IS_COMMENT = "IS_COMMENT";
        String IS_ONLY_MEMBERS = "IS_ONLY_MEMBERS";
    }

    public interface REQUEST_CODE {
        int REQUEST_CODE_LOGIN = 100;
        int FILTER_REQUEST_CODE = 101;
        int REQUEST_GALLERY = 102;
        int REQUEST_CAMERA = 103;
        int REQUEST_CODE_VOICE_SEARCH = 104;
        int REQUEST_CODE_FIRST_LOGIN = 105;

        int REQUEST_CODE_WRITE_SD_CARD = 200;
        int REQUEST_CODE_LOCATION_ACCESS = 201;
        int REQUEST_CODE_READ_PHONE_STATE = 202;
        int REQUEST_CODE_JOYRIDE = 203;


        int REQUEST_TEXT_IMAGE = 1012;
        int REQUEST_ADD_TAG = 1013;
        int REQUEST_CROPPER = 1014;
        int REQUEST_ROOM_NAME = 1015;
        int REQUEST_ROOM_PRIVACY = 1016;
        int REQUEST_EDIT_ROOM = 1017;
        int REQUEST_ROOM_MEMBER = 1018;
        int REQUEST_NOTES_PAYMENT = 1019;

        int REQUEST_NOTES_DETAILS = 8001;
        int REQUEST_NOTES_ATTACHMENTS = 8002;
        int NOTES_UPDATED = 8003;

        int REQUEST_EDIT_PROFILE = 8004;
        int REQUEST_ADD_INTEREST = 8005;
        int REQUEST_ADD_SKILLS = 8006;

        int REQUEST_CHOOSE_AVATAR = 8007;
        int REQUEST_CREATE_POST = 8008;
        int REQUEST_POST_DETAIL = 8009;
        int FLAG_POST = 8010;
        int REQUEST_REFRESH = 8011;
        int COMMENT_UPDATE = 8012;
        int ROOM_UPDATED = 8013;
    }

    public interface PERMISSION_REQUEST {
        int REQUEST_MEDIA_PERMISSION = 2011;
        int REQUEST_ALL_PERMISSION = 2012;
        int REQUEST_CONTACT_PERMISSION = 2013;

    }


    public interface RESPONSE_CODE {
        int RESPONSE_SUCCESS = 200;
        int RESPONSE_AUTH_ERROR = 106;
        int RESPONSE_AUTH_ERROR_2 = 108;
        int UNIVERSITY_INACTIVE = 214;
        int UNIVERSITY_INACTIVE_USER = 215;
        int UNIVERSITY_DELETE = 103;
        int RESPONSE_AUTH_EMAIL_ERROR = 110;
        int RESPONSE_AUTH_PASSWORD_ERROR = 111;
        int RESPONSE_AUTH_RESET_EMAIL_ERROR = 103;
        int BANK_DETAILS_NOT_ADDED = 199;
        int EMAIL_CHECK_FAIL = 101;
        int LOGIN_ID_ERROR = 400;
        int UNVI_CHECK = 107;
    }

    public interface VIEW_TYPE {
        int TEXT = 0;
        int MEDIA = 1;
        int POLL = 2;
        int MEET_UP = 3;
        int MORE = 4;
        int HEADER = 5;
        int COMMENT = 6;


    }

    public interface ROOM_TYPE {

        int PUBLIC = 0;
        int PRIVATE = 1;
        int HIDDEN = 2;
    }

    public interface ROOM_REQUEST {
        int NO_REQUESTED = -1;
        int PENDING = 0;
        int ACCEPTED = 1;
        int DECLINED = 2;
    }

    public interface AVATAR {
        int IMAGE = 1;
        int EMOJI = 2;
        int NAME = 3;
    }

    public interface ATTACHMENT {
        int FILE = 0;
        int URL = 1;
    }

    public static final int POLL_TV_BM = 8;
    public static final int POLL_LET_H = 48;
    public static final int POLL_ET_BM = 20;

    public static int MIN_NO_ET = 2; //NOSONAR
    public static int MAX_CHOICES = 10;//NOSONAR

    public static int RECORD_PER_PAGE = 10;//NOSONAR

    public interface URLS {
        String PRIVACY = "adminTurtle/#/admin/privacy";
        String TERMS_AND_CONDITIONS = "adminTurtle/#/admin/terms";
    }

    public interface PurchaseNotes {
        String FROM_POST_NOTES = "FROM_POST_NOTES";
        String FROM_NOTES_LISTING = "FROM_NOTES_LISTING";
        String FROM_MY_NOTES = "FROM_MY_NOTES";
    }

    public interface LikeStatus {
        int LIKED = 1;
        int NOT_LIKED = 0;
    }

    public interface Notification {
        String MESSAGE = "MESSAGE";
        String EVENT_NOTIFICATION = "EVENT_NOTIFICATION";
        String RESET_NOTIF_COUNT = "RESET_NOTIF_COUNT";
    }

    public static final String POST_BROADCAST = "POST_BROADCAST";

    public interface SearchExtraElementType {
        int ROOM = 0;
        int BUTTON = 1;
        int CATEGORY_HEADER = 2;
        int NEW_ROOM = 3;
        int POPULAR_ROOM = 4;
    }

    public interface UserAddedToRoomStatus {
        String NOT_INVITED = "-1";
        String PENDING = "0";
        String ADDED = "1";
    }

    public interface UserAddedRoomStatus {
        int NOT_INVITED = -1;
        int REJECTED = 2;
        int PENDING = 0;
        int ADDED = 1;
    }

    public static final String STRIPE = "pk_live_DmS66jNhPcMHWOaZvUSrNsMc";


    public static String IS_FROM_ROOM = "is_from_room";//NOSONAR
    public static String IS_FROM_POST = "is_from_post";//NOSONAR
    public static String IS_FROM_COMMENT = "is_from_comment";//NOSONAR
    public static String IS_BACK = "is_back";//NOSONAR
    public static String IS_POLL_CHOICE = "is_poll_choice";//NOSONAR

    public interface reactions {
        int notLiked = 0;
        int like = 1;
        int heart = 2;
        int angry = 3;
        int surprised = 4;
        int tear = 5;
        int grin = 6;

    }

    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String BADGE = "badge";
    public static final String SENDERID = "senderId";
    public static final String SENDERIDS = "senderIds";
    public static final String ATTACHEMENTS = "attachements";
    public static final String ATTACHEMENTS_URL = "attachment-url";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION_TYPE = "notificationType";
    public static final String NOTEID = "noteId";
    public static final String POSTID = "postId";
    public static final String ROOMID = "roomId";

}
