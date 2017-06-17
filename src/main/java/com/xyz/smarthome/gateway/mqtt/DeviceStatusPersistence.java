package com.xyz.smarthome.gateway.mqtt;

import com.xyz.smarthome.gateway.pojo.domain.Device;
import com.xyz.smarthome.gateway.pojo.type.DeviceType;
import com.xyz.smarthome.gateway.repository.DeviceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by lenovo on 2017/6/16.
 * 设备状态持久化
 */
@Component
@Order(1)
public class DeviceStatusPersistence implements DeviceStatusProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceStatusPersistence.class);

    @Autowired
    private DeviceDao deviceDao;

    @Transactional
    @Override
    public void processDeviceStatus(String gatewayNo, String deviceNo, DeviceType deviceType, String status) {
        Device gateway = deviceDao.findOne(gatewayNo);
        if (gateway == null) {
            LOGGER.warn("网关" + gatewayNo + "尚未添加归属集成商");
            return;
        }

        Device device = new Device();
        device.setDeviceNo(deviceNo);
        device.setGatewayNo(gatewayNo);
        device.setVendorId(gateway.getVendorId());
        device.setStatus(status);
        device.setUpdateTime(new Date());
        device.setDeviceType(deviceType.name());
        LOGGER.info("保存设备信息：" + device);
        try {
            deviceDao.save(device);
        } catch (Exception e) {
            LOGGER.error("保存设备信息出错：" + device, e);
        }

    }
}
