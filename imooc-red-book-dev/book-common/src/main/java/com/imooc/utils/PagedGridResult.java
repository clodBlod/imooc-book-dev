package com.imooc.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 *
 * @Title: PagedGridResult.java
 * @Package com.imooc.utils
 * @Description: 用来返回分页Grid的数据格式
 * Copyright: Copyright (c) 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PagedGridResult {
    private int page;   // 当前页数
    private long total; // 总页数
    private long records;   // 总记录数
    private List<?> rows;   // 每行显示的内容
}
