package com.xyz.smarthome.gateway.controller;

import com.xyz.smarthome.gateway.mqtt.MqttSession;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import com.xyz.smarthome.gateway.pojo.type.ResultCode;
import com.xyz.smarthome.gateway.pojo.vo.BaseResponse;
import com.xyz.smarthome.gateway.pojo.vo.BindGatewayVo;
import com.xyz.smarthome.gateway.service.DeviceService;
import com.xyz.smarthome.gateway.service.VendorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lenovo on 2017/6/17.
 * 手机API服务接口
 */
@RestController
@RequestMapping(value = "api", method = RequestMethod.POST)
public class AppApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppApiController.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private MqttSession mqttSession;

    @ApiOperation("网关绑定集成商")
    @RequestMapping("bind")
    public BaseResponse bindGateway(@RequestBody BindGatewayVo request) {
        LOGGER.info("绑定网关：" + request);
        try {
            Vendor vendor = vendorService.authenticateVendor(request.getAccessToken());
            deviceService.bindGateway(vendor, request.getGatewayNo());
            return new BaseResponse(ResultCode.SUCCESS, "绑定成功");
        } catch (Exception e) {
            LOGGER.error("网关绑定出错", e);
            return new BaseResponse(ResultCode.FAILED, e.getMessage());
        }
    }

    @ApiOperation("发送数据到Mqtt服务器")
    @RequestMapping("send")
    public BaseResponse sendMessage(@ApiParam(value = "MQTT主题", example = "gateway/000000dd/update") @RequestParam("topic") String topic,
                                    @ApiParam(value = "发送消息", example = "550007000111d5084a0000060092") @RequestParam("message") String message,
                                    @ApiParam(value = "发送模式:0-字符串;1-字节", example = "0", allowableValues = "0,1") @RequestParam("mode") int mode) {
        LOGGER.info("发送数据到MQTT服务器，主题：" + topic + "，消息：" + message);
        try {
            if (mode == 1) {
                mqttSession.sendMessage(topic, Hex.decodeHex(message.toCharArray()));
            } else {
                mqttSession.sendMessage(topic, message.getBytes());
            }
            return new BaseResponse(ResultCode.SUCCESS, "发送成功");
        } catch (Exception e) {
            LOGGER.info("数据发送失败", e);
            return new BaseResponse(ResultCode.FAILED, "数据发送失败：" + e.getMessage());
        }
    }

    @ApiOperation("接收设备状态通知消息")
    @RequestMapping("receive")
    public BaseResponse receiveMessage(@RequestBody String message) {
        LOGGER.info("收到设备状态通知消息：" + message);
        return new BaseResponse(ResultCode.SUCCESS, "接收成功");
    }
}