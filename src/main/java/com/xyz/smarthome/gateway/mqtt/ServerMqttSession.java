package com.xyz.smarthome.gateway.mqtt;

import com.xyz.smarthome.gateway.exception.ApiException;
import com.xyz.smarthome.gateway.exception.ExcepFactor;
import com.xyz.smarthome.gateway.pojo.type.ConnStatus;
import lombok.Setter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lenovo on 2017/6/12.
 * 服务端MQTT会话
 */
public class ServerMqttSession implements MqttSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMqttSession.class);

    // 消息至少成功发送一次
    private static final int QOS = 1;

    // 会话心跳间隔时间，单位秒
    private static final int KEEP_ALIVE_INTERVAL = 60;

    // 连接超时时间，单位秒
    private static final int CONNECT_TIMEOUT = 20;

    // 连接失败后重连MQTT服务时间间隔
    private static final int RECONNECT_INTERVAL = 1;

    // 服务端告警主题
    private static final String SERVER_ALARM_TOPIC = "server/alarm";

    // MQTT客户端
    private MqttClient mqttClient;

    // MQTT客户端连接设置
    private MqttConnectOptions connOpts;

    // MQTT服务器地址
    @Setter
    private String mqttBroker;

    // MQTT服务器登陆用户名
    @Setter
    private String username;

    // MQTT服务器登陆密码
    @Setter
    private String password;

    // MQTT客户端标识
    @Setter
    private String clientId;

    // 订阅主题
    @Setter
    private String subscribeTopic;

    // 连接断开后是否清除会话
    @Setter
    private boolean cleanSession = true;

    // MQTT服务器消息处理
    @Setter
    private MessageHandler messageHandler;


    // MQTT客户端回调函数
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topicName, MqttMessage message) throws Exception {
            //subscribe后得到的消息会执行到这里面
            LOGGER.info("收到MQTT[" + topicName + "]主题消息: "  + message);
            try {
                if (messageHandler != null) {
                    messageHandler.handleMessage(topicName, message.toString().toLowerCase());
                }
            }catch (Exception e) {
                LOGGER.error("消息处理出错：" + message, e);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //publish后会执行到这里
            LOGGER.info("deliveryComplete:" + token.getMessageId());
        }

        @Override
        public void connectionLost(Throwable cause) {
            // 连接丢失后，一般在这里面进行重连
            LOGGER.error("与MQTT服务器连接丢失，尝试重连", cause);
            // 重连
            connect();
        }
    };

    /**
     * 初始化MQTT客户端信息
     */
    @Override
    public void init() {
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(cleanSession);
        connOpts.setConnectionTimeout(CONNECT_TIMEOUT);
        connOpts.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        connOpts.setWill(SERVER_ALARM_TOPIC, (clientId+ ":" + ConnStatus.OFFLINE).getBytes(), QOS, true);

        new Thread(() -> connect()).start();
    }

    /**
     * 连接MQTT服务器
     */
    private void connect() {
        while (mqttClient == null || !mqttClient.isConnected()) {
            try {
                LOGGER.info("正在连接MQTT服务器: " + mqttBroker);
                if (mqttClient == null) {
                    mqttClient = new MqttClient(mqttBroker, clientId, new MemoryPersistence());
                    mqttClient.setCallback(mqttCallback);
                }
                mqttClient.connect(connOpts);
                LOGGER.info("已连上MQTT服务器");
                mqttClient.getTopic(SERVER_ALARM_TOPIC).publish((clientId + ":" + ConnStatus.OFFLINE).getBytes(), QOS, true);

                if (StringUtils.isNoneBlank(subscribeTopic)) {
                    LOGGER.info("订阅主题：" + subscribeTopic);
                    mqttClient.subscribe(subscribeTopic, QOS);
                }
            } catch (MqttException e) {
                LOGGER.error("MQTT服务器连接失败，稍后重试...");
                try {
                    Thread.sleep(RECONNECT_INTERVAL * 1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("重连线程被被中断", e1);
                }
            }
        }
    }

    @Override
    public void  sendMessage(String topic, byte[] message) {
        LOGGER.error("发送消息到[" + topic + "] HEX:" + Hex.encodeHexString(message));
        try {
            mqttClient.getTopic(topic).publish(message, QOS, true);
        } catch (MqttException e) {
            LOGGER.error("消息发送出错", e);
            throw ApiException.newInstance(ExcepFactor.DEFAULT, "MQTT服务器通信失败", e);
        }
    }

    @Override
    public void destroy() {
        if (mqttClient != null) {
            LOGGER.info("销毁mqtt会话:" + mqttBroker);
            try {
                mqttClient.disconnectForcibly();
                mqttClient.close();
            } catch (MqttException e) {
                LOGGER.error("关闭mqttClient出错", e);
            }
        }
    }


}
