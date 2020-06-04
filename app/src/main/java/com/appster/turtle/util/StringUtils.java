/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.home.PostListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class StringUtils {

    public StringUtils() {
    }


    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().length() == 0 || s.equalsIgnoreCase("null");
    }

    public static boolean isNullOrEmpty(ArrayList arraylist) {
        return arraylist == null || arraylist.size() == 0;
    }

    public static boolean isNullOrEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmpty(Arrays[] arrays) {
        return arrays == null || arrays.length == 0;
    }

    public static boolean isValidEmail(String s) {
        if (isNullOrEmpty(s)) {
            return false;
        }
        return Pattern.compile("^([\\w-]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$").matcher(s).find();
    }

    public static boolean isValidPassword(String s) {
        if (isNullOrEmpty(s)) {
            return false;
        }

        //atleast 8 characters,  alphanumeric only
        return Pattern.compile("^([a-zA-Z0-9]){8,15}$").matcher(s).find();
    }

    public static boolean isValidRoomName(String s) {
        if (isNullOrEmpty(s)) {
            return false;
        }
//        return Pattern.compile("[A-Za-z ]*").matcher(s).find();
        return s.matches("[a-zA-Z]+"); //only strings with alphabets (not even spaces)
    }


    public static void displaySnackBar(View view, String string) {
        Snackbar snackbar = Snackbar
                .make(view, string, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }


    public static String trim(String s, String s1) {
        String s2;
        do {
            s2 = s;
            if (!s.endsWith(s1)) {
                break;
            }
            s = s.substring(0, s.length() - s1.length());
        } while (true);
        for (; s2.startsWith(s1); s2 = s2.substring(s1.length(), s2.length())) {
        }
        return s2;
    }


    public static String toCamelCase(String s) {
        String[] parts = s.split(" ");
        StringBuilder camelCaseString = new StringBuilder();
        for (String part : parts) {
            camelCaseString.append(" ").append(toProperCase(part));
        }
        return camelCaseString.toString().trim();
    }

    static String toProperCase(String s) {
        if (!StringUtils.isNullOrEmpty(s)) {
            return s.substring(0, 1).toUpperCase() +
                    s.substring(1).toLowerCase();
        }
        return "";
    }

    public static String trimFirst(String s) {
        if (!StringUtils.isNullOrEmpty(s) && s.startsWith("/"))
            return s.substring(1);

        return s;
    }

    public static boolean isNullOrEmpty(int[] array) {
        return array == null || array.length == 0;
    }


    public static <T> List<T> getList(final Class<T[]> clazz, final String json) {
        final T[] jsonToObject = new Gson().fromJson(json, clazz);
        return Arrays.asList(jsonToObject);
    }

    public static String getFullString(String prefixText, String text, String suffixText) {
        return prefixText + text + suffixText;
    }

    public static void setOnClickInTag(TextView view, String pTagString, final int pos, final Posts posts, final PostListener postListener) {
        SpannableString string = new SpannableString(pTagString);
        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);

                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            LogUtils.LOGD("Hash", String.format("Clicked %s!", tag));
                            if (!isSpecialChacter(tag)) {
                                postListener.onHasHTagClick(posts, pos, tag);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#ffad82"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;

                }
            }
            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setText(string);
        }


    }


    public static void highlightTextPart(TextView textView, String fullText, String highlightString) {

        if (StringUtils.isNullOrEmpty(fullText))
            return;


        if (StringUtils.isNullOrEmpty(highlightString) || !fullText.contains(highlightString)) {
            textView.setText(fullText);
            return;
        }


        SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);

        int lastIndex = 0;

        while (lastIndex != -1) {

            lastIndex = fullText.indexOf(highlightString, lastIndex);


            if (lastIndex != -1) {
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.YELLOW);
                spannable.setSpan(backgroundColorSpan, lastIndex, lastIndex + highlightString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                lastIndex += highlightString.length();
            }
        }

        textView.setText(spannable);
    }

    public static void greyTextPart(TextView textView, String fullText, String highlightString) {

        if (StringUtils.isNullOrEmpty(fullText))
            return;


        if (StringUtils.isNullOrEmpty(highlightString) || !fullText.contains(highlightString)) {
            textView.setText(fullText);
            return;
        }


        SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);

        int lastIndex = 0;

        while (lastIndex != -1) {

            lastIndex = fullText.indexOf(highlightString, lastIndex);


            if (lastIndex != -1) {
                ForegroundColorSpan backgroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(textView.getContext(), R.color.done_unselected_color));
                spannable.setSpan(backgroundColorSpan, lastIndex, lastIndex + highlightString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                lastIndex += highlightString.length();
            }
        }

        textView.setText(spannable);
    }

    public static boolean isSpecialChacter(String str) {
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        String hashSearchString;
        if (str.startsWith("#")) {
            hashSearchString = str.substring(1);
        } else {
            hashSearchString = str;
        }
        return regex.matcher(hashSearchString).find();
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatLargeInt(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatLargeInt(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatLargeInt(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


}
