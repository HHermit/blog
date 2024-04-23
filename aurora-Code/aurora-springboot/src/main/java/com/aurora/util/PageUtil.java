package com.aurora.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

/**
 *  @description: 分页工具类
 *  
*/
public class PageUtil {

    /**
     * Page<?> 类型的对象被存储在 ThreadLocal 中，将给定的分页对象设置为当前线程的当前分页，以便在当前线程的任何地方都可以访问到。
     */
    private static final ThreadLocal<Page<?>> PAGE_HOLDER = new ThreadLocal<>();

    public static void setCurrentPage(Page<?> page) {
        PAGE_HOLDER.set(page);
    }

    public static Page<?> getPage() {
        Page<?> page = PAGE_HOLDER.get();
        if (Objects.isNull(page)) {
            setCurrentPage(new Page<>());
        }
        return PAGE_HOLDER.get();
    }

    public static Long getCurrent() {
        return getPage().getCurrent();
    }

    public static Long getSize() {
        return getPage().getSize();
    }

    /**
     * 用于获取数据库查询的偏移量（即 LIMIT 子句中的起始位置）。因为数据库索引要从0开始，而前端分页组件从1开始，所以需要减1
     */
    public static Long getLimitCurrent() {
        return (getCurrent() - 1) * getSize();
    }

    /**
     * 移除当前线程的分页对象
     */
    public static void remove() {
        PAGE_HOLDER.remove();
    }

}
