package com.xyz.smarthome.gateway.pojo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lenovo on 2017/6/15.
 * 设备表
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "deviceNo")
@Entity
@Table(name = "device", indexes = {@Index(name = "idx_device_status_gateway_no", columnList = "gateway_no")})
public class Device {

    @Id
    @Column(name = "device_no", length = 50)
    private String deviceNo;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "gateway_no", length = 50)
    private String gatewayNo;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "update_time")
    private Date updateTime;

}
