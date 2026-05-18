package com.crm.demo.common;

public class R<T> {
    private int code;
    private String message;
    private T data;

    public R() {}

    public R(int code, String message, T data) {
        this.code = code; this.message = message; this.data = data;
    }

    public int getCode() { return code; } public void setCode(int v) { this.code = v; }
    public String getMessage() { return message; } public void setMessage(String v) { this.message = v; }
    public T getData() { return data; } public void setData(T v) { this.data = v; }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static R<Void> ok() {
        return new R<>(200, "success", null);
    }

    public static R<Void> fail(String message) {
        return new R<>(500, message, null);
    }

    public static R<Void> fail(int code, String message) {
        return new R<>(code, message, null);
    }
}
