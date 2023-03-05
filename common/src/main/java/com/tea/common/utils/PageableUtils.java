package com.tea.common.utils;

import org.springframework.data.domain.Pageable;

public final class PageableUtils {
    public static final Integer DEFAULT_PAGE_NUMBER = 0;
    public static final Integer DEFAULT_PAGE_SIZE = 25;

    private PageableUtils() {}

    public static Pageable page(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return Pageable.ofSize(pageSize).withPage(pageNumber);
    }
}
