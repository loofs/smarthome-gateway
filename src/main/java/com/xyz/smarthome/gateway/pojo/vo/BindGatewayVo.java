package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lenovo on 2017/6/17.
 */
@Data
public class BindGatewayVo {

    @ApiModelProperty(value = "访问令牌", example = "3c44e029144e4c9dbd3736dce37f0657", required = true)
    private String accessToken;

    @ApiModelProperty(value = "网关ID", example = "000000dd", required = true)
    private String gatewayNo;
}
