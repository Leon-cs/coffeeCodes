package com.coffeeCodes.server.config;

import com.coffeeCodes.common.CoffeeCodeConstant;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ChangSheng on 2017/1/19 21:18.
 */
@Configuration
public class AppConfig {

    public ServiceInfo getServiceInfo() {
        return new ServiceInfo(CoffeeCodeConstant.SERVICE_NAME_CN, CoffeeCodeConstant.SERVICE_DESCRIPTION,
                CoffeeCodeConstant.SERVICE_NAME, CoffeeCodeConstant.VERSION);
    }
}
