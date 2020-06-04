/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.model;

/**
 * Created by rohantaneja on 28-Aug-2017
 */

public class RoomInfoModel {

    private int itemType; //0 for radioButton, 1 for switch, 2 for addButton
    private String itemTitle;
    private String itemDescription;
    private int itemImageResourceId;

    public RoomInfoModel(int itemType, String itemTitle, String itemDescription, int itemImageResourceId) {
        this.itemType = itemType;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemImageResourceId = itemImageResourceId;
    }

    public int getItemType() {
        return itemType;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemImageResourceId() {
        return itemImageResourceId;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemImageResourceId(int itemImageResourceId) {
        this.itemImageResourceId = itemImageResourceId;
    }
}
