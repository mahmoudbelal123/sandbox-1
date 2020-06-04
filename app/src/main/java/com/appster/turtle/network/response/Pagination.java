/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network.response;

public class Pagination {


    private int currentPage;
    private int currentSize;
    private int numberOfElements;
    private int totalPages;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
