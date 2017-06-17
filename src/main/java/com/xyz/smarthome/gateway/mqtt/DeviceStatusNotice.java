package com.xyz.smarthome.gateway.mqtt;

import com.xyz.smarthome.gateway.pojo.domain.Device;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import com.xyz.smarthome.gateway.pojo.type.DeviceType;
import com.xyz.smarthome.gateway.repository.DeviceDao;
import com.xyz.smarthome.gateway.repository.VendorDao;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by lenovo on 2017/6/15.
 */
@Component
@Order(2)
public class DeviceStatusNotice implements DeviceStatusProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceStatusNotice.class);

    private static final String CHAR_ENCODING = "UTF-8";

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private VendorDao vendorDao;

    // HTTP客户端
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public void processDeviceStatus(String gatewayNo, String deviceNo, DeviceType deviceType, String status) {
        Device gateway = deviceDao.findOne(gatewayNo);
        if (gateway == null) {
            return;
        }

        Vendor vendor = vendorDao.findOne(gateway.getVendorId());
        if (vendor == null) {
            LOGGER.error("未找到集成商" + gateway.getVendorId() + "信息，网关数据有误：" + gateway);
            return;
        }

        HttpPost httpPost = new HttpPost(vendor.getNotifyUrl());
        httpPost.setHeader("AccessToken", vendor.getAccessToken());
        JSONObject msg = new JSONObject();
        msg.put("deviceId", deviceType == DeviceType.GATEWAY ? gatewayNo : gatewayNo + "-" + deviceNo);
        msg.put("status", status);
        httpPost.setEntity(new StringEntity(msg.toString(), CHAR_ENCODING));
        LOGGER.info("推送设备状态消息给" + vendor.getUsername() + "，url=" + vendor.getNotifyUrl() + ", msg=" + msg);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                LOGGER.info("收到应答：" + EntityUtils.toString(entity, CHAR_ENCODING));
            }
        } catch (Exception e) {
            LOGGER.error("设备状态消息推送给" + vendor.getUsername() + "出错，url=" + vendor.getNotifyUrl(), e);
        }
    }

}
