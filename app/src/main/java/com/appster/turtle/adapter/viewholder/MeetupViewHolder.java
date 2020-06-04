package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemMeetupBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.rooms.LikedByActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.StringUtils;

import java.util.List;

/**
 * Created by rohantaneja on 05-Sep-2017
 * view holder of media
 */

public class MeetupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final PostListener postListener;
    private ItemMeetupBinding meetupBinding;
    private Context mContext;
    private Resources mResources;
    private String query = "";

    public MeetupViewHolder(ViewDataBinding viewDataBinding, PostListener postListener) {
        super(viewDataBinding.getRoot());
        meetupBinding = (ItemMeetupBinding) viewDataBinding;
        this.postListener = postListener;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ItemMeetupBinding getMeetupBinding() {
        return meetupBinding;
    }


    public void bindData(final Posts post, final Context context) {
        mContext = context;
        mResources = itemView.getResources();

        meetupBinding.tvPostText.setOnClickListener(this);
        meetupBinding.clReplyCommentLike.tvCommentCount.setText(String.valueOf(post.getLikesCount()));


        handlePostLocation(post);
        showHashtags(post);
        handlePostStatus(getAdapterPosition(), post, context);

        meetupBinding.ivMenu.setOnClickListener(view -> postListener.onMenuClicked(post, getAdapterPosition()));

        meetupBinding.tvRoom.setOnClickListener(view -> {


            Bundle bundle = new Bundle();

            bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
            bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);


        });

        meetupBinding.getRoot().setOnClickListener(view -> postListener.onClick(post));


        meetupBinding.clReplyCommentLike.ivComment.setOnClickListener(view -> postListener.onCommentClicked(post));

        meetupBinding.clReplyCommentLike.tvCommentCount.setOnClickListener(view -> postListener.onCommentClicked(post));


    }

    private void showHashtags(Posts post) {
        List<Tag> tagList = post.getTags();

        StringBuilder tagsDisplayString = new StringBuilder();


        for (int index = 0; index < Math.min(tagList.size(), 2); index++) {
            tagsDisplayString.append(mResources.getString(R.string.hash)).append(tagList.get(index).getValue()).append(mResources.getString(R.string.space));
        }

        if (StringUtils.isNullOrEmpty(tagsDisplayString.toString()))
            meetupBinding.tvHaghTag.setVisibility(View.GONE);
        else {
            meetupBinding.tvHaghTag.setVisibility(View.VISIBLE);
            StringUtils.highlightTextPart(meetupBinding.tvHaghTag, meetupBinding.tvHaghTag.getText().toString(), Constants.POST_QUERY);
            StringUtils.setOnClickInTag(meetupBinding.tvHaghTag, tagsDisplayString.toString(), getAdapterPosition(), post, postListener);

        }


    }


    private void handlePostLocation(final Posts post) {
        String str = mContext.getString(R.string.wants_to_meetUp) + " " + post.getPostDetail().getTitle() + " " + post.getPostDetail().getMeetupData();
        SpannableString ss = new SpannableString(str);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_semibold));

        int wantsToStartIndex = str.indexOf(post.getPostDetail().getTime());
        int wantsToEndIndex = str.indexOf(post.getPostDetail().getTime()) + post.getPostDetail().getTime().length();

        int dateIndex = wantsToEndIndex + 3;
        int dateEndIndex = dateIndex + post.getPostDetail().getDate().length() + 1;
        int addressStart = str.indexOf(post.getPostDetail().getAddress());
        int addressEnd = addressStart + post.getPostDetail().getAddress().length();


        if (query != null && !query.isEmpty()) {
            int index = TextUtils.indexOf(str.toLowerCase(), query.toLowerCase());
            while (index >= 0) {

                ss.setSpan(new BackgroundColorSpan(ContextCompat.getColor(mContext, R.color.text_highlight)), index, index + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = TextUtils.indexOf(str.toLowerCase(), query.toLowerCase(), index + query.length());
            }
        }

        ss.setSpan(new CustomTypefaceSpan("", font), wantsToStartIndex, wantsToEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", font), dateIndex, dateEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", font), addressStart, addressEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                try {
                    String uri = Uri.parse("geo:" + post.getPostDetail().getLat() + "," + post.getPostDetail().getLon() + "?q=" + post.getPostDetail().getLat() + "," + post.getPostDetail().getLon()).toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mContext.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            //to remove underline from clickable text
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
                ds.setUnderlineText(false);
            }
        };


        ss.setSpan(clickableSpan, addressStart, addressEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        meetupBinding.tvPostText.setText(ss);
        meetupBinding.tvPostText.setMovementMethod(LinkMovementMethod.getInstance());
        meetupBinding.tvPostText.setHighlightColor(Color.TRANSPARENT);
    }

    private void handlePostStatus(final int position, final Posts post, final Context context) {
        if (post.getPostDetail().isMeetupResponded()) {
            if (Constants.USER_MEETUP_RESPONSE.fromValue(post.getPostDetail().getUserResponse()).equals(Constants.USER_MEETUP_RESPONSE.ATTENDING)) {
                meetupBinding.tvAttending.setVisibility(View.VISIBLE);
                meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));
                meetupBinding.tvInterested.setVisibility(View.GONE);
                meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon_active), null, null, null);
            } else if (Constants.USER_MEETUP_RESPONSE.fromValue(post.getPostDetail().getUserResponse()).equals(Constants.USER_MEETUP_RESPONSE.INTERESTED)) {
                meetupBinding.tvAttending.setVisibility(View.GONE);
                meetupBinding.tvInterested.setVisibility(View.VISIBLE);
                meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));
                meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon_active), null, null, null);
            } else {
                meetupBinding.tvAttending.setVisibility(View.VISIBLE);
                meetupBinding.tvInterested.setVisibility(View.VISIBLE);
                meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.gray));
                meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.gray));
                meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon), null, null, null);
                meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon), null, null, null);
            }
        } else {
            meetupBinding.tvAttending.setVisibility(View.VISIBLE);
            meetupBinding.tvInterested.setVisibility(View.VISIBLE);
            meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.gray));
            meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.gray));
            meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon), null, null, null);
            meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon), null, null, null);
        }

        meetupBinding.tvAttending.setOnClickListener(view -> {

            if (post.getPostDetail().isMeetupResponded()) {

                if (Constants.USER_MEETUP_RESPONSE.fromValue(post.getPostDetail().getUserResponse()).equals(Constants.USER_MEETUP_RESPONSE.RESET)) {
                    meetupBinding.tvInterested.setVisibility(View.GONE);
                    meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));


                    //change attending icon
                    meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon_active), null, null, null);
                    //hit api (send false because you don't want to reset options)
                    postListener.onMeetupStatusChange(position, post.getPostId(), 0, false);
                } else {
                    meetupBinding.tvInterested.setVisibility(View.VISIBLE);
                    meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.gray));


                    //reset icons
                    meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon), null, null, null);
                    meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon), null, null, null);
                    //hit api (send true to reset options)
                    postListener.onMeetupStatusChange(position, post.getPostId(), -1, true);
                }
                //make interested icon visible
            } else {
                //hide interested icon
                meetupBinding.tvInterested.setVisibility(View.GONE);
                meetupBinding.tvAttending.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));


                //change attending icon
                meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon_active), null, null, null);
                //hit api (send false because you don't want to reset options)
                postListener.onMeetupStatusChange(position, post.getPostId(), 0, false);
            }

        });

        meetupBinding.tvInterested.setOnClickListener(view -> {

            if (post.getPostDetail().isMeetupResponded()) {

                if (Constants.USER_MEETUP_RESPONSE.fromValue(post.getPostDetail().getUserResponse()).equals(Constants.USER_MEETUP_RESPONSE.RESET)) {
                    meetupBinding.tvAttending.setVisibility(View.GONE);
                    meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));

                    meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon_active), null, null, null);
                    //hit api (send false because you don't want to reset options)
                    postListener.onMeetupStatusChange(position, post.getPostId(), 2, false);

                } else {
                    //make attending icon visible
                    meetupBinding.tvAttending.setVisibility(View.VISIBLE);
                    meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.gray));


                    //reset icons
                    meetupBinding.tvAttending.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_attending_icon), null, null, null);
                    meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon), null, null, null);
                    //hit api (send true to reset options)
                    postListener.onMeetupStatusChange(position, post.getPostId(), -1, true);
                }
            } else {
                //hide attending icon
                meetupBinding.tvAttending.setVisibility(View.GONE);
                meetupBinding.tvInterested.setTextColor(ContextCompat.getColor(context, R.color.dark_bg));

                //change interested icon
                meetupBinding.tvInterested.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_interested_icon_active), null, null, null);
                //hit api (send false because you don't want to reset options)
                postListener.onMeetupStatusChange(position, post.getPostId(), 2, false);
            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_likes:
                ExplicitIntent.getsInstance().navigateTo((BaseActivity) mContext, LikedByActivity.class);
                break;

            default:
                break;
        }
    }


}
