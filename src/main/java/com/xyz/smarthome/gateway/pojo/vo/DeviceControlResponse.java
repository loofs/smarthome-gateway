package com.xyz.smarthome.gateway.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by lenovo on 2017/6/17.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DeviceControlResponse extends BaseResponse{

    @ApiModelProperty(value = "操作下发成功的设备ID列表")
    private List<String> deviceReturnList;

    public DeviceControlResponse(String code, String desc, List<String> successDevices) {
        super(code, desc);
        this.deviceReturnList = successDevices;
    }


}
