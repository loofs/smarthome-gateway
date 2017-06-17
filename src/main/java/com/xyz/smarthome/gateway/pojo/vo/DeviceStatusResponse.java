package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Created by lenovo on 2017/6/16.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DeviceStatusResponse extends BaseResponse {

    @ApiModelProperty(value = "设备状态键值对")
    private Map deviceKVList;

    public DeviceStatusResponse(String code, String desc, Map deviceStatus) {
        super(code, desc);
        this.deviceKVList = deviceStatus;
    }
}
