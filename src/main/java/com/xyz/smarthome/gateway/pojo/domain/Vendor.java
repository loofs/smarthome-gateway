package com.xyz.smarthome.gateway.pojo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lenovo on 2017/6/6.
 * 厂商表
 */
@Getter
@Setter
@ToString(of = {"id", "userLabel", "username"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "vendor", uniqueConstraints = {@UniqueConstraint(name = "uk_vendor_username", columnNames = {"username"})})
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_label", length = 100)
    private String userLabel;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length = 50, nullable = false)
    private String password;

    @Column(name = "access_token", length = 50)
    private String accessToken;

    private String remarks;

    @Column(name = "notify_url", length = 50)
    private String notifyUrl;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

}
