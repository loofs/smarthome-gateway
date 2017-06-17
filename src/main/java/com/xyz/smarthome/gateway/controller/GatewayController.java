package com.xyz.smarthome.gateway.controller;

import com.xyz.smarthome.gateway.exception.ApiException;
import com.xyz.smarthome.gateway.exception.ExcepFactor;
import com.xyz.smarthome.gateway.pojo.domain.Device;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import com.xyz.smarthome.gateway.pojo.type.DeviceType;
import com.xyz.smarthome.gateway.pojo.type.ResultCode;
import com.xyz.smarthome.gateway.pojo.vo.*;
import com.xyz.smarthome.gateway.service.DeviceService;
import com.xyz.smarthome.gateway.service.VendorService;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/6/6.
 * API网关
 */
@RestController
@RequestMapping(value = "IHIterface",  method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
public class GatewayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private VendorService vendorService;

    @Autowired
    private DeviceService deviceService;

    @ApiOperation(value = "登录获取AccessToken")
    @RequestMapping("login.action")
    public LoginResponse login(@RequestBody LoginVo loginVo) {
        LOGGER.info("用户登录：" + loginVo);
        try {
            String accessToken = vendorService.login(loginVo.getUsername(), loginVo.getPassword());
            return new LoginResponse(ResultCode.SUCCESS, "登陆成功", accessToken);
        } catch (Exception e) {
            LOGGER.error("用户登陆出错：" + loginVo, e);
            return new LoginResponse(getExceptionCode(e), e.getMessage(), null);
        }
    }

    @ApiOperation(value = "查询设备状态")
    @RequestMapping("getDeviceStatus.action")
    public DeviceStatusResponse getDeviceStatus(@RequestHeader("AccessToken") String accessToken, @RequestBody String request) {
        // 保存设备状态查询结果
        Map<String, String> deviceStatus = new HashMap<>();

        try {
            Vendor vendor = vendorService.authenticateVendor(accessToken);

            JSONArray deviceList = request2Json(accessToken, request).getJSONArray("deviceList");
            LOGGER.info("查询设备状态：" + deviceList);
            for (Object deviceId : deviceList) {
                List<Device> devices = deviceService.getDevice(vendor, (String) deviceId);
                LOGGER.info("设备" + deviceId + "查询结果：" + devices);
                for (Device device : devices) {
                    if (device.getDeviceType().equals(DeviceType.GATEWAY.name())) {
                        deviceStatus.put(device.getDeviceNo(), device.getStatus());
                    } else {
                        deviceStatus.put(device.getGatewayNo() + "-" + device.getDeviceNo(), device.getStatus());
                    }
                }
            }
            return new DeviceStatusResponse(ResultCode.SUCCESS, "查询成功", deviceStatus);
        } catch (Exception e) {
            LOGGER.error("查询设备状态出错", e);
            return new DeviceStatusResponse(getExceptionCode(e), e.getMessage(), deviceStatus);
        }

    }

    @ApiOperation(value = "控制设备")
    @RequestMapping(value = "setDeviceStatus.action")
    public DeviceControlResponse setDeviceStatus(@RequestHeader("AccessToken") String accessToken, @RequestBody String request) {
        JSONArray successDevices = new JSONArray();
        try {
            Vendor vendor = vendorService.authenticateVendor(accessToken);

            JSONObject deviceActions = request2Json(accessToken, request).getJSONObject("deviceKVList");
            if (deviceActions.isNullObject()) {
                throw ApiException.newInstance(ExcepFactor.BAD_PARAM, "缺少参数:deviceKVList", null);
            }
            LOGGER.info("控制设备：" + deviceActions);

            for (Object deviceId : deviceActions.keySet()) {
                String action = (String) deviceActions.get(deviceId);
                deviceService.controlDevice(vendor, (String) deviceId, action);
                successDevices.add(deviceId);
            }
            return new DeviceControlResponse(ResultCode.SUCCESS, "操作成功", successDevices);
        } catch (Exception e) {
            LOGGER.error("控制设备出错", e);
            return new DeviceControlResponse(getExceptionCode(e), e.getMessage(), successDevices);
        }
    }


    /**
     * 字符串请求转换成JSON对象
     * @param accessToken
     * @param request
     * @return
     */
    private JSONObject request2Json(String accessToken, String request) {
        // 待解密
        String plainRequest = request;
        try {
            return JSONObject.fromObject(plainRequest);
        } catch (JSONException e) {
            throw ApiException.newInstance(ExcepFactor.BAD_PARAM, "JSON格式错误", e);
        }
    }

    /**
     * 获取异常编码
     * @param e
     * @return
     */
    private String getExceptionCode(Exception e) {
        if (e instanceof ApiException) {
            return ((ApiException) e).getExcepFactor().getCode();
        }
        return ResultCode.FAILED;
    }

}
