package com.xyz.smarthome.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by lenovo on 2017/6/6.
 * 异常因子
 */
@Getter
public class ExcepFactor {

    private String code;

    private HttpStatus httpStatus;

    private String message;

    private ExcepFactor(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static final ExcepFactor DEFAULT =
            new ExcepFactor("0", HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");

    public static final ExcepFactor USER_PASS_ERROR =
            new ExcepFactor("-1", HttpStatus.FORBIDDEN, "用户名或密码错误");

    public static final ExcepFactor IP_FORBIDDEN =
            new ExcepFactor("-2", HttpStatus.FORBIDDEN, "非法IP请求");

    public static final ExcepFactor PERMISSION_DENIED =
            new ExcepFactor("-3", HttpStatus.FORBIDDEN, "账户权限不足");

    public static final ExcepFactor DEVICE_NOT_EXIST =
            new ExcepFactor("-1", HttpStatus.NOT_FOUND, "deviceID不存在");

    public static final ExcepFactor ACCESS_TOKEN_ERROR =
            new ExcepFactor("-4", HttpStatus.SERVICE_UNAVAILABLE, "accessToken错误");

    public static final ExcepFactor DECRYPT_ERROR =
            new ExcepFactor("-5", HttpStatus.BAD_REQUEST, "解密错误");

    public static final ExcepFactor BAD_PARAM =
            new ExcepFactor("-9", HttpStatus.BAD_REQUEST, "参数错误");


}
