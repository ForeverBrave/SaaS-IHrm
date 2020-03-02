package com.ihrm.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    //总条数
    private Long total;
    //数据列表
    private List<T> rows;
}
