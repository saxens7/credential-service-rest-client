/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.credential.client.rest;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.credential.config.CredentialServiceClientProperties;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.PublicKeyResponse;
import com.dell.cpsd.credential.model.rest.api.response.SecretResponse;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;
import com.dell.cpsd.credential.util.AsymmetricCipherManagerUtil;
import com.dell.cpsd.credential.util.ErrorMessages;
import com.dell.cpsd.credential.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The credential service implementation class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class CredentialServiceClientImpl implements CredentialServiceClient
{
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AsymmetricCipherManagerUtil asymmetricCipherManagerUtil;

    @Autowired
    AsymmetricCipherManager asymmetricCipherManager;

    @Autowired
    CredentialServiceClientProperties properties;

    private String credentialServiceContext;
    private String publicKeyUri;
    private String secretStoreKeyUri;
    private String secretStoreIdUri;

    @PostConstruct
    public void init() {
        credentialServiceContext = "https://"+properties.getHostName()+":"+properties.getPort();
        publicKeyUri = "/secretstore/v1/publickey";
        secretStoreKeyUri = "/secretstore/v1/key";
        secretStoreIdUri = "/secretstore/v1/secret/{secretId}";
    }

    @PreDestroy
    public void cleanUp(){
        credentialServiceContext = null;
        publicKeyUri = null;
        secretStoreKeyUri = null;
        secretStoreIdUri = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPublicKey() throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+publicKeyUri;
        try
        {
            PublicKeyResponse publicKeyResponse = restTemplate.getForObject(URL, PublicKeyResponse.class);
            return publicKeyResponse.getPublicKey();
        }
        catch (RestClientException clientException)
        {
            throw new CredentialServiceClientException(ErrorMessages.GET_PUBLIC_KEY_FAILED.toString(), clientException);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretStoreResponse getSecret(final String secretKey, final String publicKey) throws CredentialServiceClientException
    {
        return this.getSecret(secretKey, null, publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretStoreResponse getSecret(final String secretKey, final String role, final String publicKey)
            throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreKeyUri;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-PublicKey", publicKey);
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(headers);

        UriComponentsBuilder builder;
        if (StringUtils.isEmpty(role))
        {
            builder = UriComponentsBuilder.fromUriString(URL).queryParam("key", secretKey);
        }
        else
        {
            builder = UriComponentsBuilder.fromUriString(URL).queryParam("key", secretKey).queryParam("role", role);
        }

        SecretStoreResponse storeResponse = null;
        try
        {
            ResponseEntity<SecretStoreResponse> response = restTemplate
                    .exchange(builder.toUriString(), HttpMethod.GET, entity, SecretStoreResponse.class);

            storeResponse = response.getBody();
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.GET_SECRET_BY_KEY_FAILED.processException(clientException);
        }
        return storeResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretStoreResponse getDecryptedSecret(final String secretKey)
            throws CredentialServiceClientException
    {
        SecretStoreResponse secretStoreResponse = this.getSecret(secretKey, asymmetricCipherManager.getPublicKeyEncodedBase64());
        //Decrypt
        return decryptSecretStoreResponse(secretStoreResponse);
    }

    @Override
    public SecretStoreResponse getDecryptedSecret(final String secretKey, final String role) throws CredentialServiceClientException
    {
        SecretStoreResponse secretStoreResponse = this.getSecret(secretKey, role, asymmetricCipherManager.getPublicKeyEncodedBase64());
        //Decrypt
        return decryptSecretStoreResponse(secretStoreResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretStoreResponse getSecret(final Long secretId, final String publicKey) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreIdUri;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-PublicKey", publicKey);
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(headers);

        Map<String, Long> params = new HashMap<>();
        params.put("secretId", secretId);

        SecretStoreResponse storeResponse = null;
        try
        {
            ResponseEntity<SecretStoreResponse> response = restTemplate
                    .exchange(URL, HttpMethod.GET, entity, SecretStoreResponse.class, params);

            storeResponse = response.getBody();
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.GET_SECRET_BY_SECRET_ID_FAILED.processException(clientException);
        }
        return storeResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecretStoreResponse getDecryptedSecret(final Long secretId)
            throws CredentialServiceClientException
    {
        SecretStoreResponse secretStoreResponse = this.getSecret(secretId, asymmetricCipherManager.getPublicKeyEncodedBase64());
        //Decrypt
        return decryptSecretStoreResponse(secretStoreResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String saveSecret(final SecretRequest secretRequest, Boolean encryptCredentialElement) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreKeyUri;

        //Process Secret request Object.
        SecretRequest request = this.deepCopy(secretRequest);
        if (encryptCredentialElement)
        {
            processSecretRequest(request);
        }

        String secretId = null;
        try
        {
            SecretStoreResponse response = restTemplate.postForObject(URL, request, SecretStoreResponse.class);
            String secretIdUrl = response.getLinks().get(0).getHref();
            return secretIdUrl.substring(secretIdUrl.lastIndexOf('/') + 1);
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.SAVE_SECRET_FAILED.processException(clientException);
        }

        return secretId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String updateSecret(final SecretRequest secretRequest, Boolean encryptCredentialElement) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreKeyUri;

        //Process Secret request Object.
        SecretRequest request = this.deepCopy(secretRequest);
        if (encryptCredentialElement)
        {
            processSecretRequest(request);
        }

        String secretId = null;
        try
        {
            SecretStoreResponse response = restTemplate.patchForObject(URL, request, SecretStoreResponse.class);
            String secretIdUrl = response.getLinks().get(0).getHref();
            secretId = secretIdUrl.substring(secretIdUrl.lastIndexOf('/') + 1);
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.UPDATE_SECRET_FAILED.processException(clientException);
        }
        return secretId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSecret(final String secretKey) throws CredentialServiceClientException
    {

        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreKeyUri;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL).queryParam("secretKey", secretKey);

        SecretStoreResponse storeResponse = null;
        try
        {
            restTemplate.delete(builder.toUriString());
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.DELETE_SECRET_BY_KEY_FAILED.processException(clientException);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSecret(final Long secretId) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = credentialServiceContext+secretStoreIdUri;

        Map<String, Long> params = new HashMap<>();
        params.put("secretId", secretId);

        try
        {
            restTemplate.delete(URL, params);
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.DELETE_SECRET_BY_SECRET_ID_FAILED.processException(clientException);
        }
    }

    private void processSecretRequest(SecretRequest secretRequest) throws CredentialServiceClientException
    {
        //Invalid Request body
        if (null == secretRequest)
        {
            throw new CredentialServiceClientException(ErrorMessages.NULL_POST_REQUEST_PAYLOAD.toString());
        }
        else
        {
            if (!ObjectUtils.isEmpty(secretRequest.getCredentialElement()))
            {
                try
                {
                    //Before getting public key check Credential Element Object is Encryptable or not
                    JsonNode jsonNodeObject = JsonUtil.convertObjectToJsonNode(secretRequest.getCredentialElement());
                    if(jsonNodeObject.size() > 0){
                        //ToDo - Get PublicKey from REST Service
                        String publicKey = this.getPublicKey();

                        Object credentialElement = asymmetricCipherManagerUtil
                                .encryptCredentialElement(publicKey, secretRequest.getCredentialElement());
                        secretRequest.setCredentialElement(credentialElement);
                    }
                }
                catch (IOException e)
                {
                    //ToDo - Log Invalid Error Message
                    throw new CredentialServiceClientException(ErrorMessages.ENCRYPTION_TRANSFORMATION_ERROR.toString());
                }
            }
        }
    }

    private SecretStoreResponse decryptSecretStoreResponse(SecretStoreResponse secretStoreResponse) throws CredentialServiceClientException{
        List<SecretResponse> secretResponses = secretStoreResponse.getSecrets();

        for(SecretResponse secretResponse : secretResponses){
            Object credentialElement = secretResponse.getCredentialElement();
            secretResponse.setCredentialElement(asymmetricCipherManagerUtil.decryptCredentialElement(credentialElement));
        }

        return secretStoreResponse;
    }

    private SecretRequest deepCopy(SecretRequest secretRequest) throws CredentialServiceClientException
    {
        try
        {
            return JsonUtil.deepCopy(secretRequest, SecretRequest.class);
        }
        catch (IOException e)
        {
            throw new CredentialServiceClientException(e.getLocalizedMessage(), e);
        }
    }
}
