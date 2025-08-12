package com.platform.marketing.util;

import java.util.UUID;

public class ResponseEntity<T> {
    private int code;
    private String message;
    private T data;
    private String requestId;
    private long timestamp;

    public ResponseEntity(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.requestId = UUID.randomUUID().toString();
    }

    public ResponseEntity(String status, String message, T data) {
        this.code = "success".equals(status) ? 0 : 1;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.requestId = UUID.randomUUID().toString();
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(0, "success", data);
    }

    public static <T> ResponseEntity<T> success(String message, T data) {
        return new ResponseEntity<>(0, message, data);
    }

    public static <T> ResponseEntity<T> error(String message) {
        return new ResponseEntity<>(1, message, null);
    }

    public static <T> ResponseEntity<T> fail(int code, String message) {
        return new ResponseEntity<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
