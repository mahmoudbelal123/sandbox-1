/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

import com.appster.turtle.network.BaseResponse;

import java.util.ArrayList;

/**
 * Created by atul on 04/09/17.
 * To inject activity reference.
 */

public class PostsResponse extends BaseResponse {

    ArrayList<Posts> data;
    //    ArrayList<AllPostsResponse> data;
    Pagination pagination;

    //    public ArrayList<AllPostsResponse> getData() {
    public ArrayList<Posts> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

}

//PostsResponse -- Data & Pagination
//Data -- List<AllPostsResponse>
//AllPostsResponse -- userBlocked & List<Posts>