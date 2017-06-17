package com.xyz.smarthome.gateway.mqtt;

/**
 * Created by lenovo on 2017/6/12.
 */
public interface MqttSession {

    /**
     * 初始化会话
     */
    void init();

    /**
     * 发送消息
     * @param topic
     * @param message
     */
    void sendMessage(String topic, byte[] message);

    /**
     * 销毁会话
     */
    void destroy();
}
