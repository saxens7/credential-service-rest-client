/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential;

import com.dell.cpsd.credential.client.rest.CredentialServiceClient;
import com.dell.cpsd.credential.config.CredentialServiceClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ObjectUtils;

/**
 * The credential service Autoconfigure class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
@ConditionalOnClass(CredentialServiceClient.class)
@EnableConfigurationProperties(CredentialServiceRestClientProperties.class)
public class CredentialServiceClientAutoConfigure
{
    @Autowired
    private CredentialServiceRestClientProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public CredentialServiceClientProperties getProperties()
    {
        String hostName = ObjectUtils.isEmpty(properties.getHostName()) == true ? "credential-service.cpsd.dell" : properties.getHostName();
        String port = ObjectUtils.isEmpty(properties.getPort()) == true ? "9090" : properties.getPort();

        CredentialServiceClientProperties properties = new CredentialServiceClientProperties();
        properties.setHostName(hostName);
        properties.setPort(port);

        return properties;
    }

}
