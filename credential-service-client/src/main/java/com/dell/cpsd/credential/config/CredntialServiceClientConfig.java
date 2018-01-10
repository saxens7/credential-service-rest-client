/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.config;

import com.dell.cpsd.credential.client.rest.CredentialServiceClient;
import com.dell.cpsd.credential.client.rest.CredentialServiceClientImpl;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * The credential service client Configuration.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class CredntialServiceClientConfig
{
    @Bean
    CredentialServiceClient credentialServiceClient(){
        return new CredentialServiceClientImpl();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }
}
