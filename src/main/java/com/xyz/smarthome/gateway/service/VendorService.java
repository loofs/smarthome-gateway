package com.xyz.smarthome.gateway.service;

import com.xyz.smarthome.gateway.pojo.domain.Vendor;

/**
 * Created by lenovo on 2017/6/6.
 * 厂家服务接口
 */
public interface VendorService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    String login(String username, String password);

    /**
     * 验证用户身份
     * @param accessToken
     * @return
     */
    Vendor authenticateVendor(String accessToken);

}
