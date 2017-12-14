/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.SymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.exception.CipherManagerException;
import com.dell.cpsd.credential.client.rest.CredentialServiceClient;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * The credential manager service exception class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class Application
{
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    CredentialServiceClient credentialServiceClient;

    public static void main(String args[])
    {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    @Bean
    public AsymmetricCipherManager asymmetricCipherManager() throws CipherManagerException
    {
        AsymmetricCipherManager asymmetricCipherManager = new AsymmetricCipherManager();
        asymmetricCipherManager.setSymmetricCipherManager(new SymmetricCipherManager());
        asymmetricCipherManager.initialize();
        return asymmetricCipherManager;
    }

    @Bean
    public CommandLineRunner run() throws Exception
    {
        return args ->
        {
            getPublicKey();
//            getSecretByKey();
//            getSecretById();
//            saveSecret();
//            updateSecret();
//            deleteSecretByKey();
//            deleteSecretById();
        };
    }



    //Integration Test
    private void getPublicKey(){
        System.out.println("Started......");
        try
        {
            String publicKey = credentialServiceClient.getPublicKey();
            System.out.println("publicKey -> " + publicKey);
        }
        catch (CredentialServiceClientException ex)
        {
            System.out.println(" CredentialServiceClientException " + ex.getMessage());
        }
    }
    private void getSecretByKey(){
        System.out.println("Started......");
        try {
            String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6fje7eDj6GhuiGrIRDQek3C/LYLKk69OLqHTBfgJspKO5IEEZsZLCFQOi6BughtZlYS5SsDWqTZ/jB0KHICxHDqgjSyd4xMQVDG8ARxbTufN3OyL0k0GK5a6L/wJjgu1Inx2MHofkRuIemM9/JxepmY6l9zsZPIJZYrQ0iJ4+QZ0ggwQNKnUT5UNVrkTIYsi/ZckFeB/6mYLqOXmA1w0OBCi1EvQN7V5ixWNcj2Kdx3xu6OY4By2afEb0eXe6u05RfU0R3UvQPmu15nrXNpPTLe5yr0EJOcnjKZpM39bohn8hkeopwO1nRSNOXLPnbyHHne0MpWQuwjmN2UX/HBP4QIDAQAB";
            SecretStoreResponse storeResponse = credentialServiceClient.getSecretByKey(publicKey, "key_141220017_1");
            System.out.println("secretId -> " + storeResponse.toString());
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
    private void getSecretById(){
        System.out.println("Started......");
        try {
            String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6fje7eDj6GhuiGrIRDQek3C/LYLKk69OLqHTBfgJspKO5IEEZsZLCFQOi6BughtZlYS5SsDWqTZ/jB0KHICxHDqgjSyd4xMQVDG8ARxbTufN3OyL0k0GK5a6L/wJjgu1Inx2MHofkRuIemM9/JxepmY6l9zsZPIJZYrQ0iJ4+QZ0ggwQNKnUT5UNVrkTIYsi/ZckFeB/6mYLqOXmA1w0OBCi1EvQN7V5ixWNcj2Kdx3xu6OY4By2afEb0eXe6u05RfU0R3UvQPmu15nrXNpPTLe5yr0EJOcnjKZpM39bohn8hkeopwO1nRSNOXLPnbyHHne0MpWQuwjmN2UX/HBP4QIDAQAB";
            SecretStoreResponse storeResponse = credentialServiceClient.getSecretBySecretId(publicKey, 453l);
            System.out.println("secretId -> " + storeResponse.toString());
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
    private void saveSecret(){
        System.out.println("Started......");
        try {
            SecretRequest request = new SecretRequest();
            request.setKey("key_141220017_1");
            Map<String, String> credentialElement = new HashMap<>();
            credentialElement.put("user", "ABC");
            credentialElement.put("pwd", "XYZ");
            request.setCredentialElement(credentialElement);
            String secretId = credentialServiceClient.saveSecret(request);
            System.out.println("secretId -> " + secretId);
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
    private void updateSecret(){
        System.out.println("Started......");
        try {
            SecretRequest request = new SecretRequest();
            request.setKey("key_141220017_1");
            request.setRole("admin");
            Map<String, String> credentialElement = new HashMap<>();
            credentialElement.put("user", "ABC");
            credentialElement.put("pwd", "XYZ");
            request.setCredentialElement(credentialElement);
            String secretId = credentialServiceClient.updateSecret(request);
            System.out.println("secretId -> " + secretId);
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
    private void deleteSecretByKey(){
        System.out.println("Started......");
        try {
            credentialServiceClient.deleteSecretByKey("key_141220017_1");
            System.out.println("Done");
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
    private void deleteSecretById(){
        System.out.println("Started......");
        try {
            credentialServiceClient.deleteSecretBySecretId(453l);
            System.out.println("Done");
        } catch(CredentialServiceClientException ex) {
            System.out.println(" CredentialServiceClientException "+ ex.getMessage());
        }
    }
}