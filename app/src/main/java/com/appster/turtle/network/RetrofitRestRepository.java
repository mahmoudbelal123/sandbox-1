/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network;

import retrofit2.Retrofit;

public class RetrofitRestRepository {

    private final ApiService apiService;

    public RetrofitRestRepository(Retrofit retrofit) {
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

}
