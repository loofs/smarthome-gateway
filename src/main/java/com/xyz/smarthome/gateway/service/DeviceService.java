package com.xyz.smarthome.gateway.service;

import com.xyz.smarthome.gateway.pojo.domain.Device;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;

import java.util.List;

/**
 * Created by lenovo on 2017/6/16.
 * 设备服务接口类
 */
public interface DeviceService {

    /**
     * 绑定网关
     * @param vendor
     * @param gatewayNo
     */
    void bindGateway(Vendor vendor, String gatewayNo);

    /**
     * 获取设备信息
     * @param vendor 集成商
     * @param deviceId 设备ID：网关ID-末端设备ID，当只包含网关ID时表示获取网关下面所有末端设备状态，
     * @return
     */
    List<Device> getDevice(Vendor vendor, String deviceId);

    /**
     * 控制设备
     * @param vendor 集成商
     * @param deviceId 设备ID：网关ID-末端设备ID，必须包含末端设备ID
     * @param action 设备动作
     */
    void controlDevice(Vendor vendor, String deviceId, String action);
}
