package com.gitee.zjbqdzq.core;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 */
@RequiredArgsConstructor
public class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
    protected final BaseRepository<T> repository;

    @Override
    public void save(@Nonnull T data) {
        repository.save(data);
    }

    @Nonnull
    @Override
    public Optional<T> getById(@Nonnull Long id) {
        return repository.findById(id);
    }

    @Nonnull
    @Override
    public List<T> getList(@Nullable Object params) {
        return repository.findList(params);
    }

    @Nonnull
    @Override
    public Page<T> getPageList(@Nullable Object params, int page, int size) {
        return repository.findPagedList(PageRequest.of(page - 1, size), params);
    }

    @Override
    public void safeDelete(@Nonnull List<Long> idList) {
        repository.safeDelete(idList);
    }
}
