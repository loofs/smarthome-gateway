package com.xyz.smarthome.gateway.service;

import com.xyz.smarthome.gateway.exception.ApiException;
import com.xyz.smarthome.gateway.exception.ExcepFactor;
import com.xyz.smarthome.gateway.mqtt.MqttSession;
import com.xyz.smarthome.gateway.pojo.domain.Device;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import com.xyz.smarthome.gateway.pojo.type.CurtainActuatorAction;
import com.xyz.smarthome.gateway.pojo.type.DeviceType;
import com.xyz.smarthome.gateway.pojo.type.DualActuatorAction;
import com.xyz.smarthome.gateway.repository.DeviceDao;
import com.xyz.smarthome.gateway.util.CRC8;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/6/16.
 * 设备服务接口实现类
 */
@Service
public class DeviceServiceImpl implements DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private MqttSession mqttSession;

    @Override
    public void bindGateway(Vendor vendor, String gatewayNo) {
        Device device = new Device();
        device.setDeviceNo(gatewayNo);
        device.setGatewayNo(gatewayNo);
        device.setDeviceType(DeviceType.GATEWAY.name());
        device.setVendorId(vendor.getId());
        device.setUpdateTime(new Date());
        deviceDao.save(device);
    }

    @Override
    public List<Device> getDevice(Vendor vendor, String deviceId) {
        String[] deviceIdItems = StringUtils.split(deviceId, "-");
        if (deviceIdItems.length >= 2) {
            Device device = deviceDao.findOne(deviceIdItems[1]);
            checkDeviceAuthority(deviceId, vendor, device, deviceIdItems[0]);
            return Collections.singletonList(device);
        }

        // 查询网关下所有设备状态
        Device gateway = deviceDao.findOne(deviceIdItems[0]);
        checkDeviceAuthority(deviceId, vendor, gateway, gateway.getGatewayNo());
        return deviceDao.findByGatewayNo(gateway.getDeviceNo());
    }

    @Override
    public void controlDevice(Vendor vendor, String deviceId, String action) {
        String[] deviceIdItems = StringUtils.split(deviceId, "-");

        if (deviceIdItems.length < 2) {
            throw ApiException.newInstance(ExcepFactor.BAD_PARAM, "设备ID不符合规范:" + deviceId, null);
        }

        String gatewayNo = deviceIdItems[0];
        String deviceNo = deviceIdItems[1];

        Device device = deviceDao.findOne(deviceNo);
        checkDeviceAuthority(deviceId, vendor, device, gatewayNo);

        String topic = "gateway/" + gatewayNo + "/get";
        String actionPacket = genActionPacket(deviceNo, action);
        LOGGER.info("设备" + deviceId + "动作[" + action + "]控制报文：" + actionPacket);
        try {
            mqttSession.sendMessage(topic, Hex.decodeHex(actionPacket.toCharArray()));
        } catch (DecoderException e) {
            throw ApiException.newInstance(ExcepFactor.DEFAULT, "生成控制报文出错", e);
        }

    }

    /**
     * 检查设备权限
     * @param deviceId
     * @param vendor
     * @param device
     * @param gatewayNo
     */
    private void checkDeviceAuthority(String deviceId, Vendor vendor, Device device, String gatewayNo) {
        if (device == null || !StringUtils.equals(gatewayNo, device.getGatewayNo()) ||
                !vendor.getId().equals(device.getVendorId())) {
            throw ApiException.newInstance(ExcepFactor.DEVICE_NOT_EXIST, "设备不存在：" + deviceId, null);
        }
    }


    /**
     * 生成控制设备报文
     * @param deviceNo
     * @param action
     * @return
     */
    private String genActionPacket(String deviceNo, String action) {
        String header = "55000707017a";
        String code = "";
        if (deviceNo.startsWith("6b")) {
            code = getDualActuatorActionCode(action);
        } else if (deviceNo.startsWith("8b")) {
            code = getCurtainActuatorActionCode(action);
        } else {
            throw ApiException.newInstance(ExcepFactor.BAD_PARAM, "设备[" + deviceNo + "]不支持控制动作", null);
        }

        if (code == null) {
            throw ApiException.newInstance(ExcepFactor.BAD_PARAM, "输入错误，设备[" + deviceNo + "]不支持的动作：" + action, null);
        }

        String data =  "d5" + code + "ffff00000003" + deviceNo + "ff00";
        try {
            byte crc8 = CRC8.calcCrc8(Hex.decodeHex(data.toCharArray()));
            return header + data + String.format("%02x", crc8);
        } catch (DecoderException e) {
            LOGGER.info("计算数据[" + data + "]CRC8出错", e);
            throw ApiException.newInstance(ExcepFactor.DEFAULT, "计算CRC8出错:" + e.getMessage(), e);
        }
    }


    /**
     * 获取窗帘执行器动作码
     * @param action 动作
     * @return
     */
    private String getCurtainActuatorActionCode(String action) {
        String code = null;
        if (CurtainActuatorAction.ON.name().equalsIgnoreCase(action)) {
            code = "d0";
        } else if (CurtainActuatorAction.OFF.name().equalsIgnoreCase(action)) {
            code = "d1";
        } else if (CurtainActuatorAction.STOP.name().equalsIgnoreCase(action)) {
            code = "d2";
        }
        return code;
    }


    /**
     * 获取双路控制器动作码
     * @param action 动作
     * @return
     */
    private String getDualActuatorActionCode(String action) {
        String code = null;
        if (DualActuatorAction.ON1.name().equalsIgnoreCase(action)) {
            code = "a1";
        } else if (DualActuatorAction.OFF1.name().equalsIgnoreCase(action)) {
            code = "a0";
        } else if (DualActuatorAction.ON2.name().equalsIgnoreCase(action)) {
            code = "b1";
        } else if (DualActuatorAction.OFF2.name().equalsIgnoreCase(action)) {
            code = "b0";
        }
        return code;
    }
}
