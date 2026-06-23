package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Maps Spring Data Page JSON (field "number" = current page index)
public class PageResponse<T> {

    public List<T> content;

    @SerializedName("number")
    public int page;

    public int totalPages;

    public long totalElements;

    public boolean last;
}
