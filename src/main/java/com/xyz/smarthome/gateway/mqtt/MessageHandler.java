package com.xyz.smarthome.gateway.mqtt;


/**
 * Created by lenovo on 2017/6/12.
 */
public interface MessageHandler {

    /**
     * 处理消息
     * @param topic 主题
     * @param message 消息内容
     */
    void handleMessage(String topic, String message);

}
