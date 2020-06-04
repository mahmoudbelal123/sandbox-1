/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import com.appster.turtle.model.University;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.ui.locationlog.LocationFragment;
import com.google.android.gms.location.LocationRequest;
import com.orhanobut.hawk.Hawk;

@SuppressWarnings("ALL")
public final class PreferenceUtil {

    public static final String KEY_APP_STATE = "APP_STATE";
    public static final String KEY_JOY_RIDE = "JOY_RIDE";
    public static final String KEY_TOUR_SEEN = "KEY_TOUR_SEEN";
    public static final String KEY_FIRST_TIME = "FIRST_TIME";
    public static final String LAST_SEARCH_WAS_FRIENDS = "LAST_SEARCH_WAS_FRIENDS";
    public static final String LAST_SEARCH_WAS_ALL_MEMBERS = "LAST_SEARCH_WAS_ALL_MEMBERS";
    public static final String LAST_SEARCH_WAS_ALL_NOTES = "LAST_SEARCH_WAS_ALL_NOTES";

    private PreferenceUtil() {

    }

    public static void saveAppState(Object state) {
        Hawk.put(KEY_APP_STATE, state);
    }

    public static Object getAppState() {
        return Hawk.get(KEY_APP_STATE);
    }

    public static boolean isJoyRideComplete() {
        return Hawk.get(KEY_JOY_RIDE, false);
    }

    public static void setJoyRideComplete(boolean value) {
        Hawk.put(KEY_JOY_RIDE, value);
    }

    public static boolean isFirstTimeLaunch() {
        return Hawk.get(KEY_FIRST_TIME, false);
    }

    public static void setFirstTime(boolean value) {
        Hawk.put("first", value);
    }

    public static boolean isFirstTime() {
        return Hawk.get("first", true);
    }

    public static void setFirstTimeLaunch(boolean value) {
        Hawk.put("first", value);
    }

    public static float getDistance() {
        return Hawk.get("distance", LocationFragment.MINIMUM_DISPLACEMENT);
    }

    public static int getNotification() {
        return Hawk.get("notification", 0);
    }

    public static void setDistance(float value) {
        Hawk.put("distance", value);
    }

    public static void setNotificationCount(int value) {
        Hawk.put("notification", value);
    }

    public static int getTime() {
        return Hawk.get("time", (int) LocationFragment.UPDATE_INTERVAL_IN_MILLISECONDS);
    }

    public static void setTime(int value) {
        Hawk.put("time", value);
    }

    public static int getPriority() {
        return Hawk.get("priority", LocationRequest.PRIORITY_LOW_POWER);
    }

    public static void setPriority(int value) {
        Hawk.put("priority", value);
    }

    public static void setTotalCard(int count) {
        Hawk.put("card", count);
    }

    public static void reset() {
        Hawk.deleteAll();
    }

    //    public static void resetButSaveEmail(Context context) {
    public static void resetButSaveEmail() {
//        SharedPreferences preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        preferences.edit().putString("userEmail", getUser().getLoginId()).apply();
        String email = getUser().getEmail();
        Hawk.deleteAll();
        saveUserEmail(email);
    }

    public static void resetButSaveEmailAndToken() {
//        SharedPreferences preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        preferences.edit().putString("userEmail", getUser().getLoginId()).apply();
        UserBaseModel email = getUser();
        String deviceToken = Hawk.get("deviceId");
        Hawk.deleteAll();
//        sac(email);
        setUser(email);
        saveDeviceToken(deviceToken);
    }

    private static void saveDeviceToken(String deviceToken) {
        Hawk.put("deviceId", deviceToken);
    }

    private static void saveUserEmail(String email) {
        Hawk.put("userEmail", email);
    }

//    public static String getUserEmail() {
//        return Hawk.get("userEmail");
//    }

//    public static String getUserEmail(Context context) {
//        return context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("userEmail", "");
//    }

    public static void setUser(UserBaseModel user) {
        Hawk.put("user", user);
    }

    public static UserBaseModel getUser() {
        return Hawk.get("user");
    }

    public static void setUniv(University university) {
        Hawk.put("university", university);
    }

    public static University getUniv() {
        return Hawk.get("university");
    }

    public static void setToken(String token) {
        Hawk.put("token", token);
    }

    public static String getToken() {
        return Hawk.get("token", "");
    }

    //for suggestive searching. (Required for minimum letters 2)
    public static void setLastSearchWasAllMembers(boolean lastSearchWasAllMembers) {
        Hawk.put(LAST_SEARCH_WAS_ALL_MEMBERS, lastSearchWasAllMembers);
    }

    //for suggestive searching. (Required for minimum letters 2)
    public static boolean wasLastSearchAllMembers() {
        return Hawk.get(LAST_SEARCH_WAS_ALL_MEMBERS);
    }

    //for suggestive searching. (Required for minimum letters 2)
    public static void setLastSearchWasAllNotes(boolean lastSearchWasAllNotes) {
        Hawk.put(LAST_SEARCH_WAS_ALL_NOTES, lastSearchWasAllNotes);
    }

    //for suggestive searching. (Required for minimum letters 2)
    public static boolean wasLastSearchAllNotes() {
        return Hawk.get(LAST_SEARCH_WAS_ALL_NOTES);
    }


    //for suggestive searching. (Required for minimum letters 2)
    public static void setLastSearchWasForFriends(boolean lastSearchWasForUsers) {
        Hawk.put(LAST_SEARCH_WAS_FRIENDS, lastSearchWasForUsers);
    }

    //for suggestive searching. (Required for minimum letters 2)
    public static boolean wasLastSearchForFriends() {
        return Hawk.get(LAST_SEARCH_WAS_FRIENDS);
    }

}
