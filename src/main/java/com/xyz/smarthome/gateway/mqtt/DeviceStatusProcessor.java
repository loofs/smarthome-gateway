package com.xyz.smarthome.gateway.mqtt;

import com.xyz.smarthome.gateway.pojo.type.DeviceType;

/**
 * Created by lenovo on 2017/6/15.
 * 设备状态处理器
 */
public interface DeviceStatusProcessor {

    /**
     * 处理设备状态
     * @param gatewayNo 网关ID
     * @param deviceNo 设备ID
     * @param deviceType 设备类型
     * @param status 设备状态
     */
    void processDeviceStatus(String gatewayNo, String deviceNo, DeviceType deviceType, String status);
}
