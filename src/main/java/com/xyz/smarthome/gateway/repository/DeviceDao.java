package com.xyz.smarthome.gateway.repository;

import com.xyz.smarthome.gateway.pojo.domain.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lenovo on 2017/6/15.
 * 设备表查询接口
 */
public interface DeviceDao extends CrudRepository<Device, String> {

    /**
     * 根据网关ID查询设备
     * @param gatewayNo
     * @return
     */
    List<Device> findByGatewayNo(String gatewayNo);
}
