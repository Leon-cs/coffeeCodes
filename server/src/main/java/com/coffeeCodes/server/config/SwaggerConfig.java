package com.coffeeCodes.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.service.ApiInfo.DEFAULT_CONTACT;

/**
 * Created by ChangSheng on 2017/1/19 16:44.
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Autowired
    AppConfig config;

    @Bean
    public Docket swaggerSpringBootPlugin() {
        ServiceInfo serviceInfo = config.getServiceInfo();
        ApiInfo apiInfo = new ApiInfo(serviceInfo.getTitle(),
                serviceInfo.getDescription(),
                serviceInfo.getVersion(),
                "urn:tos",
                DEFAULT_CONTACT,
                "Coffee Codes License",
                "http://www.changsheng.pub");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(serviceInfo.getServiceName())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/(" + serviceInfo.getServiceName() + "/).*"))
                .build()
                .apiInfo(apiInfo)
                .enable(true)
                .useDefaultResponseMessages(false);
    }
}
