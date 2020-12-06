package com.coodev.androidcollection.mvvm.page;

/**
 * Pageing三个核心类
 * {@link androidx.paging.PagedListAdapter}
 * {@link androidx.paging.PagedList}
 * {@link javax.sql.DataSource}
 * 其中 DataSource分为三种：
 * {@link androidx.paging.PositionalDataSource}
 * 可以从任意位置加载数据，数据源数量固定，例如，第一次请求 start=1&size=5，即从第一个开始往后的5条数据。
 * 第二次可以请求 start=3&size=7,即从第2条数据开始往后的7条数据
 * {@link androidx.paging.PageKeyedDataSource}
 * 适用于数据源以 页 的方式进行请求，例如：page=2&page_size=5,以5条数据为一页的数据，返回2页的5条数据
 * {@link androidx.paging.ItemKeyedDataSource}
 * 主要用于下一页的数据会依赖上一页的最后一个对象的中的某个数据后者可以
 * 常用与评论，比如上一页最后一个评论的id是222，下一页要请求从id是222后面的若干条数据。
 */
class Page {
}
