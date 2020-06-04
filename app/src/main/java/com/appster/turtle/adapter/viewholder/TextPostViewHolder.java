package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemTextPostBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.StringUtils;
/*
 * view holder of text post
 */
public class TextPostViewHolder extends RecyclerView.ViewHolder {

    private final PostListener postListener;
    private ItemTextPostBinding textPostBinding;
    private Context mContext;
    private String query = "";


    public TextPostViewHolder(Context context, ViewDataBinding viewDataBinding, PostListener postListener) {
        super(viewDataBinding.getRoot());
        textPostBinding = (ItemTextPostBinding) viewDataBinding;
        mContext = context;
        this.postListener = postListener;

        textPostBinding.tvText.setHashtagEnabled(true);
        textPostBinding.tvText.setMentionEnabled(true);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ItemTextPostBinding getTextPostBinding() {
        return textPostBinding;
    }

    public void bindData(final Posts posts) {
        textPostBinding.setPost(posts);
        textPostBinding.setUser(posts.getUsersListModel());
        textPostBinding.clReplyCommentLike.setPost(posts);

        textPostBinding.tvText.setTagList(posts.getTaggedUsers());

        textPostBinding.getRoot().setOnClickListener(view -> postListener.onClick(posts));


        textPostBinding.tvRoom.setOnClickListener(view -> {


            Bundle bundle = new Bundle();

            bundle.putInt(Constants.BundleKey.ROOM_ID, posts.getRooms().getId());
            bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);


        });

        textPostBinding.clReplyCommentLike.ivComment.setOnClickListener(view -> postListener.onCommentClicked(posts));

        textPostBinding.clReplyCommentLike.tvCommentCount.setOnClickListener(view -> postListener.onCommentClicked(posts));


        textPostBinding.ivMenu.setOnClickListener(view -> postListener.onMenuClicked(posts, getAdapterPosition()));

        textPostBinding.tvText.setQuery(query);

        textPostBinding.tvText.setOnMentionClickListener((textView, s) -> {

            int userId = 0;
            for (User user : posts.getTaggedUsers()) {
                if (user != null && user.getUserName().equalsIgnoreCase(s)) {
                    userId = user.getUserId();
                    break;
                }
                //something here
            }

            if (userId == 0)
                return null;

            Bundle i = new Bundle();
            i.putInt(Constants.USER_ID, userId);
            ExplicitIntent.getsInstance().navigateTo((BaseActivity) textView.getContext(), UserProfileActivity.class, i);


            return null;
        });

        textPostBinding.tvText.setOnHashtagClickListener((textView, s) -> {
            LogUtils.LOGD("String", s);
            if (!StringUtils.isSpecialChacter(s)) {
                postListener.onHasHTagClick(posts, getAdapterPosition(), s);
            }
            return null;
        });

    }

    private void setTagColor(TextView view, String pTagString, final int pos, final Posts posts) {
        SpannableString string = new SpannableString(pTagString);
        if (!query.isEmpty()) {

            int index = TextUtils.indexOf(string.toString().toLowerCase(), query.toLowerCase());
            while (index >= 0) {

                string.setSpan(new BackgroundColorSpan(ContextCompat.getColor(mContext, R.color.text_highlight)), index, index
                        + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = TextUtils.indexOf(string.toString().toLowerCase(), query.toLowerCase(), index + query.length());
            }
        }

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(string);


    }


}
