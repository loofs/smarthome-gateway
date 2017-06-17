package com.xyz.smarthome.gateway.exception;

/**
 * Created by lenovo on 2017/6/6.
 * API异常
 */
public class ApiException extends RuntimeException {

    private ExcepFactor excepFactor;

    public static ApiException newInstance(ExcepFactor excepFactor, String message, Throwable t) {
        ApiException exception = new ApiException(t == null ? message : message + t.getMessage(), t);
        exception.excepFactor = excepFactor;
        return exception;
    }

    private ApiException(String message, Throwable t) {
        super(message, t);
    }

    public ExcepFactor getExcepFactor() {
        return excepFactor;
    }
}
