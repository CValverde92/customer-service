package com.nttdata.customer_service.config;

import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaConfig {

    /**
     * Configuración personalizada del cliente Eureka
     */
    @Bean
    public EurekaClientConfigBean eurekaClientConfig() {

        EurekaClientConfigBean config = new EurekaClientConfigBean();
        config.setRegistryFetchIntervalSeconds(5);
        config.setInitialInstanceInfoReplicationIntervalSeconds(5);
        config.setShouldEnforceRegistrationAtInit(true);

        return config;
    }
}