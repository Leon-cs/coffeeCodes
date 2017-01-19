package com.coffeeCodes.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by ChangSheng on 2017/1/19 21:15.
 */
@AllArgsConstructor
public class ServiceInfo {
    @Getter
    private String title;
    @Getter
    private String description;
    @Getter
    private String serviceName;
    @Getter
    private String version;
}
