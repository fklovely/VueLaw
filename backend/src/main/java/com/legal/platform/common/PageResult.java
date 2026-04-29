package com.legal.platform.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;

    public PageResult(List<T> records, Long total) {
        this.records = records;
        this.total = total;
        this.size = 10L;
        this.current = 1L;
        this.pages = (total + size - 1) / size;
    }

    public PageResult(List<T> records, Long total, Long size, Long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = size > 0 ? (total + size - 1) / size : 0L;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long size, Long current) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setSize(size);
        result.setCurrent(current);
        result.setPages(size > 0 ? (total + size - 1) / size : 0L);
        return result;
    }
}
