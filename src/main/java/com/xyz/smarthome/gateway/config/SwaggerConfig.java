package com.xyz.smarthome.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by lenovo on 2017/5/20.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(regex("/.*"))//过滤的接口
                .apis(RequestHandlerSelectors.basePackage("com.xyz.smarthome.gateway.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("家佳齐智能家居平台集成商接口使用规范")//大标题
                .description("本文档提供给集成商，用于对接平台接口，方便集成商通过自有平台获取和控制物联网设备，便于开发业务平台，更好的服务于企业客户和终端用户。")//详细描述
                .version("1.0")//版本
                .termsOfServiceUrl("http://www.en-star.com/")
                .build();
    }
}