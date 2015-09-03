package com.example.lenovo.livingwhere.view;

/**
 * 下拉刷新和上拉加载坚挺
 */
public interface OnRefreshListener {

    /**
     * 下拉刷新
     */
    void onDownPullRefresh();

    /**
     * 上拉加载更多
     */
    void onLoadingMore();
}
