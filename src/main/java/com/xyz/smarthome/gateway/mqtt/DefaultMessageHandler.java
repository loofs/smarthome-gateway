package com.xyz.smarthome.gateway.mqtt;

import com.xyz.smarthome.gateway.pojo.type.DeviceType;
import org.apache.commons.codec.binary.Hex;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lenovo on 2017/6/13.
 */
@Component
public class DefaultMessageHandler implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageHandler.class);

    // 告警主题后缀
    private static final String ALARM_TOPIC_SUFFIX = "alarm";

    @Autowired
    private List<DeviceStatusProcessor> deviceStatusProcessorList;

    @Override
    public void handleMessage(String topic, String message) {
        String[] topicItems = topic.split("/");
        // 网关编号
        String gatewayNo = topicItems[1];

        DeviceType deviceType = null;
        String deviceStatus = "";
        String deviceNo = "";

        if (topic.endsWith(ALARM_TOPIC_SUFFIX)) {
            deviceType = DeviceType.GATEWAY;
            deviceNo = gatewayNo;
            deviceStatus = message.toUpperCase();
            LOGGER.info("网关" + gatewayNo + "状态：" + deviceStatus);
        } else {
            Pair<String, String> info = getMessageInfo(message);
            deviceNo = info.getValue0();
            String deviceValue = info.getValue1();
            LOGGER.info("设备ID:" + deviceNo + "，设备原始状态值：" + deviceValue);

            if (deviceNo.startsWith("6b")) {
                deviceType = DeviceType.DUAL_ACTUATOR;
                deviceStatus = getDaulActuatorStatus(deviceValue);
            } else if (deviceNo.startsWith("4a")) {
                deviceType = DeviceType.MAGNETOMETER;
                deviceStatus = getMagnetometerStatus(deviceValue);
            } else if (deviceNo.startsWith("5a")) {
                deviceType = DeviceType.SWITCH;
                deviceStatus = getSwitchStatus(deviceValue);
            } else if (deviceNo.startsWith("2a")) {
                deviceType = DeviceType.SENSIRION;
                deviceStatus = getSensirionStatus(deviceValue);
            } else if (deviceNo.startsWith("3a")) {
                deviceType = DeviceType.BODY_SENSOR;
                deviceStatus = getBodySensorStatus(deviceValue);
            } else if (deviceNo.startsWith("8b")) {
                deviceType = DeviceType.CURTAIN_ACTUATOR;
                deviceStatus = getCurtainActuatorStatus(deviceValue);
            } else {
                LOGGER.error("不能识别的设备类型，设备ID：" + deviceNo);
                return;
            }

            LOGGER.info("设备" + deviceNo + "类型为：" + deviceType);
            if (deviceStatus == null) {
                LOGGER.error("设备" + deviceNo + "不能识别的设备原始状态值" + deviceValue);
                return;
            }
        }

        if (deviceStatusProcessorList != null) {
            for (DeviceStatusProcessor processor : deviceStatusProcessorList) {
                processor.processDeviceStatus(gatewayNo, deviceNo, deviceType, deviceStatus);
            }
        }
    }

    /**
     * 获取窗帘执行器状态
     * @param deviceValue
     * @return
     */
    private String getCurtainActuatorStatus(String deviceValue) {
        String status = null;
        if (deviceValue.equals("44304f81")) {
            status = "";
        }
        return status;
    }

    /**
     * 获取人体感应器状态
     * @param deviceValue
     * @return
     */
    private String getBodySensorStatus(String deviceValue) {
        String status = null;
        if (deviceValue.equals("1c184f81")) {
            status = "";
        } else if (deviceValue.length() == 8){
            status = deviceValue.endsWith("88") ? "HUM" : "NONE";
            status += ";" + ((Integer.valueOf(deviceValue.substring(2, 6), 16) & 0xffc0) >> 6) + "lux";
        }
        return status;
    }

    /**
     * 获取湿温传感器状态
     * @param deviceValue
     * @return
     */
    private String getSensirionStatus(String deviceValue) {
        String status = null;
        if (deviceValue.equals("10084f81")) {
            status = "";
        } else if (deviceValue.length() == 8){
            status = (Integer.valueOf(deviceValue.substring(2, 4), 16) / 2.5) + "%;";
            status += (60 - 16 * Integer.valueOf(deviceValue.substring(0, 2), 16) / 51) + "℃";
        }
        return status;
    }


    /**
     * 获取开关状态
     * @param deviceValue
     * @return
     */
    private String getSwitchStatus(String deviceValue) {
        String status = null;
        if (deviceValue.endsWith("10")) {
            status = "ON1";
        } else if (deviceValue.endsWith("30")) {
            status = "OFF1";
        } else if (deviceValue.endsWith("50")) {
            status = "ON2";
        } else if (deviceValue.endsWith("70")) {
            status = "OFF2";
        }
        return status;
    }

    /**
     * 获取门磁状态
     * @param deviceValue
     * @return
     */
    private String getMagnetometerStatus(String deviceValue) {
        String status = null;
        if (deviceValue.endsWith("00")) {
            status = "";
        } else if (deviceValue.endsWith("08")) {
            status = "ON";
        } else if (deviceValue.endsWith("09")) {
            status = "OFF";
        }
        return status;
    }

    /**
     * 获取双路执行器状态
     * @param deviceValue
     * @return
     */
    private String getDaulActuatorStatus(String deviceValue) {
        String status = null;
        if (deviceValue.equals("44284f81")) {
            status = "";
        } else if (deviceValue.endsWith("1f")) {
            status = "ON;ON";
        } else if (deviceValue.endsWith("1b")) {
            status = "ON;OFF";
        } else if (deviceValue.endsWith("1d")) {
            status = "OFF;ON";
        } else if (deviceValue.endsWith("19")) {
            status = "OFF;OFF";
        }
        return status;
    }

    /**
     * 获取消息内容
     * @param message 原始消息
     * @return 设备编号、设备状态值
     */
    private Pair<String, String> getMessageInfo(String message) {
        if (message.length() >= 48) {
            return Pair.with(message.substring(22, 30), message.substring(14, 22));
        }
        return Pair.with(message.substring(16, 24), message.substring(14, 16));
    }
}
