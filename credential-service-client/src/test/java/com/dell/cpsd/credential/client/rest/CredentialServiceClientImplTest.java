/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.credential.client.rest;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.SymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.exception.CipherManagerException;
import com.dell.cpsd.credential.config.CredentialServiceClientProperties;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;
import com.dell.cpsd.credential.util.AsymmetricCipherManagerUtil;
import com.dell.cpsd.credential.util.ErrorMessages;
import com.dell.cpsd.credential.util.JsonUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * The Credential Service Client Implementation Test.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CredentialServiceClientImplTest
{
    @InjectMocks
    CredentialServiceClientImpl   credentialServiceClient;

    @Mock
    RestTemplate                  restTemplate;

    @Mock
    CredentialServiceClientProperties properties;

    private MockRestServiceServer mockServer;

    @Mock
    AsymmetricCipherManagerUtil   asymmetricCipherManagerUtil;

    @Mock
    AsymmetricCipherManager       asymmetricCipherManager;

    String                        secretStoreUri;
    String                        publicKeyUri;
    String                        secretStoreIdUri;
    Long                          secretId;

    Map<String, String>           credentialElement;
    String                        saveOrUpdateSecretResponse;
    String                        getSecretByKeyResponse;
    String                        getSecretBySecretIdResponse;
    String                        getDecryptedSecretBySecretIdResponse;
    String                        getDecryptedSecretBySecretKeyResponse;
    String                        responseSecretId;
    String                        secretKey;
    String                        publicKeyResponseObject;
    SecretRequest                 secretRequest;
    String                        publickKey;
    String                        publickKeyInvalid;
    String                        role;
    Map<String, String>           credentialElementEncrypted;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException, CipherManagerException
    {
        secretStoreIdUri = "/secretstore/v1/secret/{secretId}";
        secretStoreUri = "/secretstore/v1/key";
        publicKeyUri = "/secretstore/v1/publickey";
        secretKey = "key_191220017_1";
        secretStoreIdUri = "/secretstore/v1/secret/";
        role="user";
        secretId = 459l;

        restTemplate = new RestTemplate();
        HelperUtility.accessPrivateFields(CredentialServiceClientImpl.class, credentialServiceClient, "restTemplate", restTemplate);
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        asymmetricCipherManager = new AsymmetricCipherManager();
        asymmetricCipherManager.setSymmetricCipherManager(new SymmetricCipherManager());
        asymmetricCipherManager.initialize();
        HelperUtility.accessPrivateFields(CredentialServiceClientImpl.class, credentialServiceClient, "asymmetricCipherManager",
                asymmetricCipherManager);

        credentialElement = new HashMap<>();
        credentialElement.put("user", "ABC");
        credentialElement.put("pwd", "XYZ");

        secretRequest = new SecretRequest();
        secretRequest.setCredentialElement(credentialElement);

        publickKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6fje7eDj6GhuiGrIRDQek3C/LYLKk69OLqHTBfgJspKO5IEEZsZLCFQOi6BughtZlYS5SsDWqTZ/jB0KHICxHDqgjSyd4xMQVDG8ARxbTufN3OyL0k0GK5a6L/wJjgu1Inx2MHofkRuIemM9/JxepmY6l9zsZPIJZYrQ0iJ4+QZ0ggwQNKnUT5UNVrkTIYsi/ZckFeB/6mYLqOXmA1w0OBCi1EvQN7V5ixWNcj2Kdx3xu6OY4By2afEb0eXe6u05RfU0R3UvQPmu15nrXNpPTLe5yr0EJOcnjKZpM39bohn8hkeopwO1nRSNOXLPnbyHHne0MpWQuwjmN2UX/HBP4QIDAQAB";
        publickKeyInvalid = "XXXXXXX______";
        saveOrUpdateSecretResponse = "{\"secrets\":[],\"status\":\"CREATED\",\"links\":[{\"rel\":\"secret\",\"href\":\"https://10.239.133.177:9090/secretstore/v1/secret/459\"}]}";
        getSecretByKeyResponse = "{\"secrets\":[{\"role\":\"default\",\"secretId\":459,\"keys\":[\"key_191220017_1\"],\"credentialElement\":{\"pwd\":\"ofVky1mE+b+AIoiG8GvIozs9YLw2Rg8QVKpjPNHJiLUNVAh8baqJJB3JEiJS7IIbq5/XhyJ3tEaMqLhNRLWz/gSVAA+ClkRE/sKq8Tm4xfzK/UL6YSsktmuvTbOoBM5pGDhf8FwkTzsVo2fCZH9xSth3BmrNOEs2BClRZIZz9XAXnkDDtxGiwaPhbV4vpxs1rqjDgHLWscVQhYZDyj4jxEGCjXyYE0wanssDJ3wbZpYYsiIMQPlGMuRaPCicPTzdUym8bujZXeptv6sIuLNygDdLll36OenXyaiKbSO4LkS5V67JjD5cg8y73bD/R8//xxUiOfRn+a8sqqcdIJ1QZw==\",\"user\":\"38Uw5NRxm/H5dUL5B3cdxnWDBV39NuCTVUJRcKesXsmPqzYg509MqiBOUCGBznnsV80B7ewwbBMGqraY3xWhUgKF+szEPnlhnGg4sYiBldUes2go3gukMZ9cXtV9j/4+VbHQ9UzYx41QNq9AJy+WrYOUuKXpEDwBlA43/fapN2UT2KygiIP6ATDsDWhjfzngMMh9A9sBB+mzITJtZ/U9aqjsOa9aoF39fv0Ks2BKDxwfzg9fancpa3lbXSZ12SbD74QempIutf/0h98WgMV8SdP9hx7GDdRh2896pK/YP6RQEVwqZNnroUUGJRx0LdBxmNLz87BVlLyXVB3iBI2jpg==\"}}],\"status\":\"OK\",\"links\":[]}";
        getSecretBySecretIdResponse = "{\"secrets\":[{\"role\":\"default\",\"secretId\":459,\"keys\":[\"key_191220017_1\"],\"credentialElement\":{\"pwd\":\"gzDVcvcP/UK1WTfJrDwlsnCnCIkqnFZS23BvhofdzNRbQhKDdor3zeQjUXEqF0NSXEeInw1NTbrhOOUdhWzJbgbCCK63fMr8TkzD3npcPuvrPdFv8bigy7M/acSWicZ4ylzdOoKKjgUTCNVD/8gwyWyF3o2BAMrCPcJB5j/1OFMGzM9EjrzYDyBLoBk5H1/Y3Vb8ysDr5kws80Xrs+Kx/jOxxX2+xWa3Z6vl49zTDRwu42AdRb3RvwJPNgHD4+NjsyvgKpR1riIuVUpfZXWNdIErrxFema49Y9/zIMxL4aHChopN92BEy1vFQhGYcmWhKH9uxHIv1SnSBsYs+xz8ng==\",\"user\":\"jRyatAGA6PeNRoHeu8+nO/cKywWnV8Tijpgo8aOYcmHQDaMJMcgnxE0tgGcsA+k3qad6l8kLqEm4z9tSauVsm7mkowTjbO8YyB5JtjEuGia3xotubKXRJi26d/qkHwWPWAfA5e2SQF4WJ/Fv/7Y2oIva5rgfJ0LjIlznePvZ963becLI4HzjW1YpUEbE/cLK56knJ2tTpXisRuJDFW4sKK6K1WbqhystBVFhMees2NMmIdNksxqklDvxQTsZo7ES/4rEttAILjRf4nyvVzr0TFzmmsLTUhIeeanKCfnoX4N3egEhPuuNk7U0LsL+vPGdopFAfRaF6bz0s93shHRF/g==\"}}],\"status\":\"OK\",\"links\":[]}";
        getDecryptedSecretBySecretIdResponse = "{\"secrets\":[{\"role\":\"default\",\"secretId\":459,\"keys\":[\"key_191220017_1\"],\"credentialElement\":{\"pwd\":\"XYZ\",\"user\":\"ABC\"}}],\"status\":\"OK\",\"links\":[]}";
        getDecryptedSecretBySecretKeyResponse = "{\"secrets\":[{\"role\":\"default\",\"secretId\":459,\"keys\":[\"key_191220017_1\"],\"credentialElement\":{\"pwd\":\"XYZ\",\"user\":\"ABC\"}}],\"status\":\"OK\",\"links\":[]}";
        publicKeyResponseObject = "{\"publicKey\":\"" + publickKey + "\"}";
        responseSecretId = "459";

        credentialElementEncrypted = new HashMap<>();
        credentialElementEncrypted.put("user",
                "Z9SXGST2KD8hDZ7voX5UwqWwX0cz8/NiWVO0pvvWxsIVnZav7C7ERT493TzNrZUpLcbbPfGOytEpcU2Q1X04H23Z9b4e2NIZOBo0i9pzH/in2w7cz+2/oZHKv4AqsdQJaXhLnCb6trxpE1T2pGkeTLud811I9XoqiSh11asPBm/TuUZdfsatRhn4vPVMwDg8UXPQtgz44inL1p6MSjFE9MvN0dKM369kZsDXfH6WMOHGVAIIjjAGoZ2Mdogypcgcl+80LtOfLb1sDkYc5yw2nhYX+YRC8/ZZRg/7Kgl4D4HcYJ65BQ/zqDxQvj2EzBd7Uxq+M/T1M/lingktb9iYgg==");
        credentialElementEncrypted.put("pwd",
                "Q+zBYsS0mYNyOwfOiDm3Q3G8hwBP8zZZMXZRfEyAkEiRut6Noh0fWgvqYq5BJAkxs+vS0DTOK1ZGi4EskIsL3BHdNu7QkZ9Y144Xb11PC/8rVQ/34KnJq1gUTC5T4O+49DxAPEzlvO5f+Ad3SfitaHfmvq5tShLjBjLPEIWFpMCzMsGSUHe5ExfMkPXYUxWV/090eit3awtF+ZFXDhT2kYY+4UT0R8HoTdabq4SVJMBsS7ZLjaaEz/wRABk4n7pjd4F9BRdeM7ZSyc/MYsddMwshEI6JxLoftYgii3K9t+bVqFI6ce+Uvqm38t7JPzxhP/DLPbzS6MMPHwreu8mPmw==");

        Mockito.when(properties.getHostName()).thenReturn("credential-service.cpsd.dell");
        Mockito.when(properties.getPort()).thenReturn("9090");

        credentialServiceClient.init();
    }

    @After
    public void tearDown()
    {
        restTemplate = null;
        mockServer = null;
        credentialElement = null;
        secretRequest = null;
        saveOrUpdateSecretResponse = null;
        publicKeyResponseObject = null;
        responseSecretId = null;
        credentialElementEncrypted = null;

        credentialServiceClient.cleanUp();
    }

    @Test
    public void testGetPublicKey() throws CredentialServiceClientException
    {
        String publicKeyURL = "/secretstore/v1/publickey";

        mockServer.expect(requestTo(Matchers.containsString(publicKeyURL))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        String publickKeyResponse = credentialServiceClient.getPublicKey();
        mockServer.verify();

        Assert.assertEquals(publickKey, publickKeyResponse);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetPublicKeyException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        credentialServiceClient.getPublicKey();
    }

    @Test
    public void testSaveSecretSuccess() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(saveOrUpdateSecretResponse, MediaType.APPLICATION_JSON));

        String secretId = credentialServiceClient.saveSecret(secretRequest, true);
        Assert.assertEquals(responseSecretId, secretId);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testSaveSecretInvalidSecretRequest() throws CredentialServiceClientException
    {
        credentialServiceClient.saveSecret(null, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testSaveSecretHandlePublicKeyException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        credentialServiceClient.saveSecret(secretRequest, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testSaveSecretHandleEncryptionException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenThrow(new CredentialServiceClientException(ErrorMessages.ENCRYPTION_TRANSFORMATION_ERROR.toString()));

        credentialServiceClient.saveSecret(secretRequest, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testSaveSecretHttpServerErrorException() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        credentialServiceClient.saveSecret(secretRequest, true);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testSaveSecretHttpClientErrorException() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.POST)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        });

        credentialServiceClient.saveSecret(secretRequest, true);
    }

    @Test
    public void testUpdateSecretSuccess() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(saveOrUpdateSecretResponse, MediaType.APPLICATION_JSON));

        String secretId = credentialServiceClient.updateSecret(secretRequest, true);
        Assert.assertEquals(responseSecretId, secretId);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testUpdateSecretInvalidSecretRequest() throws CredentialServiceClientException
    {
        credentialServiceClient.updateSecret(null, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testUpdateSecretHandlePublicKeyException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        credentialServiceClient.updateSecret(secretRequest, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testUpdateSecretHandleEncryptionException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenThrow(new CredentialServiceClientException(ErrorMessages.ENCRYPTION_TRANSFORMATION_ERROR.toString()));

        credentialServiceClient.updateSecret(secretRequest, true);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testUpdateSecretHttpServerErrorException() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.PATCH))
                .andRespond(withServerError());

        credentialServiceClient.updateSecret(secretRequest, true);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testUpdateSecretHttpClientErrorException() throws CredentialServiceClientException
    {
        Mockito.when(asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, secretRequest.getCredentialElement()))
                .thenReturn(credentialElementEncrypted);

        mockServer.expect(requestTo(Matchers.containsString(publicKeyUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(publicKeyResponseObject, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.PATCH)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        });

        credentialServiceClient.updateSecret(secretRequest, true);
    }

    @Test
    public void testGetSecretByKey() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getSecretByKeyResponse, MediaType.APPLICATION_JSON));
        SecretStoreResponse storeResponse = credentialServiceClient.getSecret(secretKey, publickKey);
        String response = JsonUtil.convertObjectToJson(storeResponse);
        Assert.assertEquals(getSecretByKeyResponse, response);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetSecretByKeyInvalidPublicKeyException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        });

        credentialServiceClient.getSecret(secretKey, publickKeyInvalid);

    }

    @Test
    public void testGetSecretByKeyAndRole() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getSecretByKeyResponse, MediaType.APPLICATION_JSON));
        String secretKey = null;
        SecretStoreResponse storeResponse = credentialServiceClient.getSecret(secretKey, role, publickKey);
        String response = JsonUtil.convertObjectToJson(storeResponse);
        Assert.assertEquals(getSecretByKeyResponse, response);

    }
    
    @Test(expected = CredentialServiceClientException.class)
    public void testGetSecretByKeyAndRoleInvalidPublicKeyException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        });

        credentialServiceClient.getSecret(secretKey, role, publickKeyInvalid);

    }

    
    @Test
    public void testGetDecryptedSecretByKey() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getDecryptedSecretBySecretKeyResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any())).thenReturn(credentialElement);

        SecretStoreResponse storeResponse = credentialServiceClient.getDecryptedSecret(secretKey);

        String response = JsonUtil.convertObjectToJson(storeResponse.getSecrets().get(0).getCredentialElement());
        String credentialElementStr = JsonUtil.convertObjectToJson(credentialElement);
        Assert.assertEquals(credentialElementStr, response);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetDecryptedSecretByKeyException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getDecryptedSecretBySecretKeyResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any()))
                .thenThrow(new CredentialServiceClientException(ErrorMessages.DECRYPTION_TRANSFORMATION_ERROR.toString()));

       credentialServiceClient.getDecryptedSecret(secretKey);

    }
    
    @Test
    public void testGetDecryptedSecretByKeyAndRole() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getDecryptedSecretBySecretKeyResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any())).thenReturn(credentialElement);

        SecretStoreResponse storeResponse = credentialServiceClient.getDecryptedSecret(secretKey, role);

        String response = JsonUtil.convertObjectToJson(storeResponse.getSecrets().get(0).getCredentialElement());
        String credentialElementStr = JsonUtil.convertObjectToJson(credentialElement);
        Assert.assertEquals(credentialElementStr, response);

    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetDecryptedSecretByKeyAndRoleException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getDecryptedSecretBySecretKeyResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any()))
                .thenThrow(new CredentialServiceClientException(ErrorMessages.DECRYPTION_TRANSFORMATION_ERROR.toString()));

      credentialServiceClient.getDecryptedSecret(secretKey, role);

    }

    
    @Test
    public void testGetSecretBySecretId() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getSecretBySecretIdResponse, MediaType.APPLICATION_JSON));

        SecretStoreResponse storeResponse = credentialServiceClient.getSecret(secretId, publickKey);
        String response = JsonUtil.convertObjectToJson(storeResponse);
        Assert.assertEquals(getSecretBySecretIdResponse, response);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetSecretBySecretIdEncryptionException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.GET)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        });

        credentialServiceClient.getSecret(secretId, publickKeyInvalid);
    }

    @Test
    public void testGetDecryptedSecretBySecretId() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getSecretBySecretIdResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any())).thenReturn(credentialElement);

        SecretStoreResponse storeResponse = credentialServiceClient.getDecryptedSecret(secretId);
        String response = JsonUtil.convertObjectToJson(storeResponse.getSecrets().get(0).getCredentialElement());
        String credentialElementStr = JsonUtil.convertObjectToJson(credentialElement);
        Assert.assertEquals(credentialElementStr, response);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testGetDecryptedSecretBySecretIdException() throws CredentialServiceClientException, IOException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getSecretBySecretIdResponse, MediaType.APPLICATION_JSON));

        Mockito.when(asymmetricCipherManagerUtil.decryptCredentialElement(Mockito.any()))
                .thenThrow(new CredentialServiceClientException(ErrorMessages.DECRYPTION_TRANSFORMATION_ERROR.toString(), new Throwable()));
        credentialServiceClient.getDecryptedSecret(secretId);
    }

    @Test
    public void testDeleteSecret() throws CredentialServiceClientException
    {

        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());
        credentialServiceClient.deleteSecret(secretId);
        mockServer.verify();
    }

    @Test
    public void testDeleteSecretException() throws CredentialServiceClientException
    {

        mockServer.expect(requestTo(Matchers.containsString(secretStoreIdUri))).andExpect(method(HttpMethod.DELETE)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        });
        try {
        credentialServiceClient.deleteSecret(secretId);
        }catch(CredentialServiceClientException credentialServiceClientException) {
            Assert.assertEquals(credentialServiceClientException.getMessage(), ErrorMessages.DELETE_SECRET_BY_SECRET_ID_FAILED.toString());
        }
    }

    @Test
    public void testDeleteBySecretKey() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        credentialServiceClient.deleteSecret(secretKey);

        mockServer.verify();
    }

    @Test
    public void testDeleteBySecretKeyException() throws CredentialServiceClientException
    {
        mockServer.expect(requestTo(Matchers.containsString(secretStoreUri))).andExpect(method(HttpMethod.DELETE)).andRespond(respond -> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        });

        try
        {
            credentialServiceClient.deleteSecret(secretKey);
        }
        catch (CredentialServiceClientException credentialServiceClientException)
        {
            Assert.assertEquals(ErrorMessages.DELETE_SECRET_BY_KEY_FAILED.toString(), credentialServiceClientException.getMessage());
        }
    }

}
