package com.gitee.zjbqdzq.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class R<T> implements Serializable {

    private String msg;
    private T data;
    private int code;
    private boolean success;

    private R(String msg, T data, int code, boolean success) {
        this.msg = msg;
        this.data = data;
        this.code = code;
        this.success = success;
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(msg, data, 0, true);
    }

    public static <T> R<T> ok(T data) {
        return R.ok(data, "ok");
    }

    public static <T> R<T> ok(String msg) {
        return R.ok(null, msg);
    }

    public static <T> R<T> ok() {
        return R.ok(null, "ok");
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(msg, null, 1, false);
    }

    public static <T> R<T> fail(String msg, int code) {
        return new R<>(msg, null, code, false);
    }

    public static <T> R<T> fail() {
        return R.fail("error");
    }
}
