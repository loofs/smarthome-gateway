package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * Created by lenovo on 2017/6/11.
 */
@Data
public class DeviceControlVo {

    private Map<String, String> deviceKVList;

}
