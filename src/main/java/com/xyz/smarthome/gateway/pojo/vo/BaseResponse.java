package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by lenovo on 2017/6/16.
 */
@Data
@AllArgsConstructor
public class BaseResponse {

    @ApiModelProperty(value = "结果编码", example = "1", required = true)
    private String code = "0";

    @ApiModelProperty(value = "结果描述")
    private String desc;
}
