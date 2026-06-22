package com.ptithcm.frontend.network.dto;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;
    public int page;
    public int totalPages;
    public long totalElements;
}