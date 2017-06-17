package com.xyz.smarthome.gateway.service;

import com.xyz.smarthome.gateway.exception.ApiException;
import com.xyz.smarthome.gateway.exception.ExcepFactor;
import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import com.xyz.smarthome.gateway.repository.VendorDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * Created by lenovo on 2017/6/6.
 * 厂商服务接口实现类
 */
@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorDao vendorDao;

    @Transactional
    @Override
    public String login(String username, String password) {

        Vendor vendor = vendorDao.findByUsername(username);
        if (vendor == null || !StringUtils.equalsIgnoreCase(vendor.getPassword(), password)) {
            throw ApiException.newInstance(ExcepFactor.USER_PASS_ERROR, "登陆失败，用户名或密码错误", null);
        }

        String accessToken = UUID.randomUUID().toString().replace("-", "");

        vendor.setAccessToken(accessToken);
        vendor.setLastLoginTime(new Date());
        vendorDao.save(vendor);

        return accessToken;
    }

    @Override
    public Vendor authenticateVendor(String accessToken) {
        Vendor vendor = vendorDao.findByAccessToken(accessToken);
        if (vendor == null) {
            throw ApiException.newInstance(ExcepFactor.ACCESS_TOKEN_ERROR, "访问令牌错误：" + accessToken + "，请重新登陆获取", null);
        }
        return vendor;
    }

}
