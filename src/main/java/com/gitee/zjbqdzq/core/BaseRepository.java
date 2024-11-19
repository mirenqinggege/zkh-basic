package com.gitee.zjbqdzq.core;

import com.gitee.zjbqdzq.core.annotation.Alias;
import com.gitee.zjbqdzq.core.annotation.Like;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public interface BaseRepository<T extends BaseEntity> extends JpaRepositoryImplementation<T, Long> {

    class Wrapper<T> {
        public T data;
    }

    @Nonnull
    @Override
    Optional<T> findById(@Nonnull Long id);

    @Nonnull
    default List<T> findList(@Nullable Object params) {
        return findAll(buildCondition(params));
    }

    @Nonnull
    default Page<T> findPagedList(@Nonnull Pageable pageable, @Nullable Object params) {
        if (Objects.isNull(params)) {
            return findAll(pageable);
        }
        Specification<T> specification;
        if (params instanceof Map<?, ?> map) {
            specification = buildConditionFromMap(map);
        } else {
            specification = buildCondition(params);
        }
        return findAll(specification, pageable);
    }

    default Specification<T> defaultCondition() {
        return (root, _, cb) -> cb.isNull(root.get("deleteTime"));
    }

    default <Y> Path<Y> getPath(Root<T> root, String[] keyPath) {
        String key = keyPath[0];
        Path<Y> path = root.get(key);
        for (int i = 1; i < keyPath.length; i++) {
            path = path.get(keyPath[i]);
        }
        return path;
    }

    @Nonnull
    default Specification<T> buildCondition(@Nullable Object params) {
        Wrapper<Specification<T>> wrap = new Wrapper<>();
        wrap.data = defaultCondition();
        if (Objects.isNull(params)) {
            return wrap.data;
        }
        ReflectionUtils.doWithFields(params.getClass(), field -> {
            String[] keyPath = getFieldKey(field);
            Object value = field.get(params);
            if (Objects.isNull(value) && field.isAnnotationPresent(Nullable.class)) {
                wrap.data = wrap.data.and((root, _, _) -> getPath(root, keyPath).isNull());
                return;
            }
            if (ObjectUtils.isEmpty(value)) {
                return;
            }
            wrap.data = wrap.data.and((root, _, cb) -> {
                if (field.isAnnotationPresent(Like.class)) {
                    Like like = field.getAnnotation(Like.class);
                    String val = String.valueOf(value);
                    if (like.left()) {
                        val = "%" + val;
                    }
                    if (like.right()) {
                        val += "%";
                    }
                    return cb.like(getPath(root, keyPath), val);
                }
                return cb.equal(getPath(root, keyPath), value);
            });

        }, AccessibleObject::trySetAccessible);

        return wrap.data;
    }

    default String[] getFieldKey(Field field) {
        if (field.isAnnotationPresent(Alias.class)) {
            return field.getAnnotation(Alias.class).value();
        } else {
            return new String[]{field.getName()};
        }
    }

    @Nonnull
    default Specification<T> buildConditionFromMap(Map<?, ?> map) {
        Specification<T> where = defaultCondition();
        Set<? extends Map.Entry<?, ?>> entries = map.entrySet();
        for (Map.Entry<?, ?> entry : entries) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (ObjectUtils.isEmpty(value)) {
                continue;
            }
            where = where.and((root, _, builder) -> {
                Path<String> path = root.get(String.valueOf(key));
                if (value instanceof String str) {
                    return builder.like(path, "%" + str + "%");
                }
                return builder.equal(path, value);
            });
        }
        return where;
    }

    @Transactional
    default void safeDelete(@Nonnull List<Long> idList) {
        List<T> allById = findAllById(idList);
        LocalDateTime now = LocalDateTime.now();
        allById.forEach(item -> item.setDeleteTime(now));
        saveAll(allById);
    }
}
