package com.gitee.zjbqdzq.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Administrator
 */
@RequiredArgsConstructor
public abstract class BaseController<T extends BaseEntity, QueryParamsVo, SaveDto> {

    protected final BaseService<T> service;
    @Autowired
    protected ObjectMapper objectMapper;


    @GetMapping("{id}")
    public R<T> getById(@PathVariable("id") Long id) {
        return R.ok(service.getById(id).orElseThrow(() -> new RuntimeException("数据不存在")));
    }

    @GetMapping("{page}/{size}")
    public R<Page<T>> findPagedList(QueryParamsVo params, @PathVariable("page") int page, @PathVariable("size") int size) {
        return R.ok(service.getPageList(params, page, size));
    }

    @GetMapping
    public R<List<T>> findAll(QueryParamsVo params) {
        return R.ok(service.getList(params));
    }

    @PostMapping
    public R<?> save(@Valid @RequestBody SaveDto data) {
        // @formatter:off
        service.save(objectMapper.convertValue(data, new TypeReference<>() {}));
        // @formatter:on
        return R.ok();
    }


    @DeleteMapping("{ids}")
    public R<?> delete(@PathVariable("ids") List<Long> ids) {
        service.safeDelete(ids);
        return R.ok();
    }
}
