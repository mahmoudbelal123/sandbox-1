package com.appster.turtle.ui.search;

import com.appster.turtle.databinding.ItemSearchPeopleMoreBinding;
import com.appster.turtle.model.User;

/**
 * Created by abhaykant on 22/02/18.
 * Search people listner
 */

public interface SearchPeopleClickListner {

    void onUserClick(ItemSearchPeopleMoreBinding item, User user, int pos);

    void onInvite(ItemSearchPeopleMoreBinding item, User user, int pos, boolean accept);
}
