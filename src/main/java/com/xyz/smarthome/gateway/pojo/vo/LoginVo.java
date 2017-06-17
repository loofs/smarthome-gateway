package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lenovo on 2017/6/16.
 */
@Data
public class LoginVo {

    @ApiModelProperty(value = "用户名", example = "fzjiajiaqi", required = true)
    private String username;

    @ApiModelProperty(value = "密码，使用MD5加密，用十六进制字符串（小写）表示", example = "900150983cd24fb0d6963f7d28e17f72")
    private String password;
}

