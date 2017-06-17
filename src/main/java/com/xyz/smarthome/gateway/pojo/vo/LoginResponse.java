package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Created by lenovo on 2017/6/6.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class LoginResponse extends BaseResponse {

    @ApiModelProperty(value = "访问令牌", example = "3c44e029144e4c9dbd3736dce37f0657", required = true)
    private String accessToken;

    public LoginResponse(String code, String desc, String accessToken) {
        super(code, desc);
        this.accessToken = accessToken;
    }

}
