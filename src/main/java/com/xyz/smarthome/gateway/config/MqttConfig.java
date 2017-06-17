package com.xyz.smarthome.gateway.config;

import com.xyz.smarthome.gateway.mqtt.ServerMqttSession;
import com.xyz.smarthome.gateway.mqtt.MessageHandler;
import com.xyz.smarthome.gateway.mqtt.MqttSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lenovo on 2017/6/17.
 */
@Configuration
public class MqttConfig {

    // 服务端MQTT客户端标识
    private static final String SERVER_CLIENT_ID = "smarthome-api-gateway";

    // 服务端订阅MQTT主题
    private static final String SERVER_SUBSCRIBE_TOPIC = "gateway/+/update/#";

    @Value("${app.mqtt.broker}")
    private String broker;

    @Value("${app.mqtt.username}")
    private String username;

    @Value("${app.mqtt.password}")
    private String password;

    /**
     * 创建服务端MQTT会话
     * @param messageHandler 消息处理器
     * @return MQTT会话对象
     */
    @Bean
    public MqttSession createServerMqttSession(MessageHandler messageHandler) {
        ServerMqttSession session = new ServerMqttSession();
        session.setMqttBroker(broker);
        session.setUsername(username);
        session.setPassword(password);
        session.setClientId(SERVER_CLIENT_ID);
        session.setSubscribeTopic(SERVER_SUBSCRIBE_TOPIC);
        session.setCleanSession(false);
        session.setMessageHandler(messageHandler);
        return session;
    }

    @Bean
    public CommandLineRunner initMqttSession(MqttSession mqttSession) {
        return args -> mqttSession.init();
    }

}
