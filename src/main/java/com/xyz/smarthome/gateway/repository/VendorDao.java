package com.xyz.smarthome.gateway.repository;

import com.xyz.smarthome.gateway.pojo.domain.Vendor;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by lenovo on 2017/6/6.
 * 厂商表访问接口
 */
public interface VendorDao extends CrudRepository<Vendor, Long> {

    /**
     * 根据用户名查询厂商信息
     * @param username
     * @return
     */
    Vendor findByUsername(String username);

    /**
     * 根据访问令牌查询用户信息
     * @param accessToken
     * @return
     */
    Vendor findByAccessToken(String accessToken);

}
