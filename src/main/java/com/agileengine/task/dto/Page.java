package com.agileengine.task.dto;

import java.util.List;

public class Page {
    private List<ImageItem> pictures;
    private Integer page;
    private Integer pageCount;
    private Boolean hasMore;

    public List<ImageItem> getPictures() {
        return pictures;
    }

    public void setPictures(List<ImageItem> pictures) {
        this.pictures = pictures;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public String toString() {
        return "Page{" +
                ", page=" + page +
                ", pageCount=" + pageCount +
                ", hasMore=" + hasMore +
                '}';
    }
}
