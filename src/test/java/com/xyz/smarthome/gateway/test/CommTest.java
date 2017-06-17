package com.xyz.smarthome.gateway.test;

import com.xyz.smarthome.gateway.util.DESUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by lenovo on 2017/6/6.
 * 公共测试类
 */
public class CommTest {

    @Test
    public void md5() {
        System.out.println(DigestUtils.md5Hex("abc"));
    }

    @Test
    public void des() {
        String desKey = "3c44e029144e4c9dbd3736dce37f0657";
        System.out.println("des key:" + desKey);
        String text = "{\n" +
                "    \"code\": \"1\",\n" +
                "    \"desc\": \"请求成功\",\n" +
                "    \"deviceKVList \": {\n" +
                "        \"000000dd-6b000010\": \"ON;ON\"\n" +
                "    }\n" +
                "}";
        String cipher = DESUtils.desEncrypt(text, desKey);
        System.out.println("cipher:" + cipher);
        System.out.println("text:" + DESUtils.desDecrypt(cipher, desKey));


        
    }

    @Test
    public void uuid() {
        System.out.println("uuid: " + UUID.randomUUID().toString().replace("-", ""));
    }


    @Test
    public void number() {
        System.out.println(StringUtils.equals(null, null));
    }

    @Test
    public void json() {
        JSONObject json = JSONObject.fromObject("sfsdf");

        System.out.println(json.getJSONArray("aa"));
        System.out.println(json.getJSONArray("aa") == null);
    }

}
