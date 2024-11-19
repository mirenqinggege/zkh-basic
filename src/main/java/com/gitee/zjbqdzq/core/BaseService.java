package com.gitee.zjbqdzq.core;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 */
public interface BaseService<T extends BaseEntity> {

    void save(@Nonnull T data);

    @Nonnull
    Optional<T> getById(@Nonnull Long id);

    @Nonnull
    List<T> getList(@Nullable Object params);

    @Nonnull
    Page<T> getPageList(@Nullable Object params, int page, int size);

    void safeDelete(@Nonnull List<Long> idList);
}
