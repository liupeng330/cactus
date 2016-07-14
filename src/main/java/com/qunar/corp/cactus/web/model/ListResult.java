package com.qunar.corp.cactus.web.model;

import com.google.common.base.Preconditions;

import java.util.List;

public class ListResult<T> {
    private int totalRow;
    private int currentPageNum;
    private int pageSize;
    private int totalPageNum;
    private int pageNumBeginIndex;
    private int pageNumEndIndex;
    private List<T> datas;
    private static final int PAGE_SCOPE = 5; // 页码显示的最大范围，如：PAGE_SCOPE=5时，页码最多显示从1-5

    public static final String DEFAULT_PAGE_SIZE = "20";

    public ListResult() {
    }

    public ListResult(int totalRow, int currentPageNum, int pageSize, int totalPageNum, List<T> datas) {
        this.totalRow = totalRow;
        this.currentPageNum = currentPageNum;
        this.pageSize = pageSize;
        this.totalPageNum = totalPageNum;
        this.datas = datas;
    }

    private int calTotalPage() {
        return (totalRow % pageSize) == 0 ? (totalRow / pageSize) : ((totalRow / pageSize) + 1);
    }

    /**
     * 设置页面显示的页码起始值和结束值
     */
    private void adjustIndex() {
        int midNum = ((PAGE_SCOPE / 2) + 1);
        int beginOffset = (PAGE_SCOPE / 2);
        int endOffset = PAGE_SCOPE / 2;
        if (currentPageNum < midNum) {
            pageNumBeginIndex = 1;
        } else {
            pageNumBeginIndex = currentPageNum - beginOffset;
        }
        if (currentPageNum + endOffset > totalPageNum) {
            pageNumEndIndex = totalPageNum;
        } else {
            pageNumEndIndex = currentPageNum + endOffset;
        }
        if (currentPageNum > totalPageNum - midNum) {
            if ((totalPageNum - PAGE_SCOPE + 1) > 0) {
                pageNumBeginIndex = totalPageNum - PAGE_SCOPE + 1;
            }
        }
        if (currentPageNum < midNum) {
            if (totalPageNum >= PAGE_SCOPE) {
                pageNumEndIndex = PAGE_SCOPE;
            }
        }

    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
        adjustIndex();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        Preconditions.checkArgument(pageSize > 0, "the argument pageSize (%s) must be greater than 0", pageSize);
        this.pageSize = pageSize;
        if (totalRow != 0) {
            totalPageNum = calTotalPage();
        }
        adjustIndex();
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    /**
     * 得到前台显示的页码起始值
     * 
     * @return 页码起始值
     */
    public int getPageNumBeginIndex() {
        return pageNumBeginIndex;
    }

    /**
     * 得到前台显示的页码结束值
     * 
     * @return 页码结束值
     */
    public int getPageNumEndIndex() {
        return pageNumEndIndex;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
