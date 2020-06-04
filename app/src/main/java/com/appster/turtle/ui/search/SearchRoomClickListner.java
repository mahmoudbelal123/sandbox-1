package com.appster.turtle.ui.search;

import com.appster.turtle.databinding.ItemSearchBinding;
import com.appster.turtle.model.SearchRoomsNewModel;

/**
 * Created  on 22/02/18 .
 */

public interface SearchRoomClickListner {

    void onRoomClick(ItemSearchBinding item, SearchRoomsNewModel roomsModel, int pos);

    void onInviteClick(ItemSearchBinding item, SearchRoomsNewModel roomsModel, int pos, boolean accept);
}
