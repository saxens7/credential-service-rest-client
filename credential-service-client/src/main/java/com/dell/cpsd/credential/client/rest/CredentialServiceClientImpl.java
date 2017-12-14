/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.client.rest;

import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.PublicKeyResponse;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;
import com.dell.cpsd.credential.util.AsymmetricCipherManagerUtil;
import com.dell.cpsd.credential.util.ErrorMessages;
import com.dell.cpsd.credential.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
@Component
public class CredentialServiceClientImpl implements CredentialServiceClient
{
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AsymmetricCipherManagerUtil asymmetricCipherManagerUtil;

    @Override
    public String getPublicKey() throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/publickey";
        try
        {
            PublicKeyResponse publickKeyResponse = restTemplate.getForObject(URL, PublicKeyResponse.class);
            return publickKeyResponse.getPublicKey();
        }
        catch (RestClientException clientException)
        {
            throw new CredentialServiceClientException(ErrorMessages.GET_PUBLIC_KEY_FAILED.toString(), clientException);
        }
    }

    @Override
    public SecretStoreResponse getSecretByKey(final String publicKey, final String secretKey) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/key";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-PublicKey", publicKey);
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL).queryParam("key", secretKey);

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

    @Override
    public SecretStoreResponse getSecretBySecretId(final String publicKey, final Long secretId) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/secret/{secretId}";

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

    @Override
    public String saveSecret(final SecretRequest secretRequest) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/key";

        //Process Secret request Object.
        processSecretRequest(secretRequest);

        String secretId = null;
        try
        {
            SecretStoreResponse response = restTemplate.postForObject(URL, secretRequest, SecretStoreResponse.class);
            String secretIdUrl = response.getLinks().get(0).getHref();
            return secretIdUrl.substring(secretIdUrl.lastIndexOf('/') + 1);
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.SAVE_SECRET_FAILED.processException(clientException);
        }

        return secretId;
    }

    @Override
    public String updateSecret(final SecretRequest secretRequest) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/key";

        //Process Secret request Object.
        processSecretRequest(secretRequest);

        String secretId = null;
        try
        {
            SecretStoreResponse response = restTemplate.patchForObject(URL, secretRequest, SecretStoreResponse.class);
            String secretIdUrl = response.getLinks().get(0).getHref();
            secretId = secretIdUrl.substring(secretIdUrl.lastIndexOf('/') + 1);
        }
        catch (RestClientException clientException)
        {
            ErrorMessages.UPDATE_SECRET_FAILED.processException(clientException);
        }
        return secretId;
    }

    @Override
    public void deleteSecretByKey(final String secretKey) throws CredentialServiceClientException
    {

        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/key";

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

    @Override
    public void deleteSecretBySecretId(final Long secretId) throws CredentialServiceClientException
    {
        //ToDo - Change it to get from capability registry / property file.
        String URL = "https://credential-service.cpsd.dell:9090/secretstore/v1/secret/{secretId}";

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
                //ToDo - Get PublicKey from REST Service
                String publicKey = this.getPublicKey();

                Object credentialElement = asymmetricCipherManagerUtil
                        .encryptCredentialElement(publicKey, secretRequest.getCredentialElement());
                secretRequest.setCredentialElement(credentialElement);
            }
        }
    }

    private void processSaveSecretResponseException(RestClientException clientException) throws CredentialServiceClientException
    {
        processResponseException(clientException, ErrorMessages.SAVE_SECRET_FAILED);
    }

    private void processUpdateSecretResponseException(RestClientException clientException) throws CredentialServiceClientException
    {
        processResponseException(clientException, ErrorMessages.UPDATE_SECRET_FAILED);
    }

    private void processDeleteSecretResponseException(RestClientException clientException) throws CredentialServiceClientException
    {
        this.processResponseException(clientException, ErrorMessages.DELETE_SECRET_BY_KEY_FAILED);
    }

    private void processGetSecretResponseException(RestClientException clientException, ErrorMessages errorMessage)
            throws CredentialServiceClientException
    {
        processResponseException(clientException, errorMessage);
    }

    private void processResponseException(RestClientException clientException, ErrorMessages errorMessage)
            throws CredentialServiceClientException
    {
        String body = ((HttpClientErrorException) clientException).getResponseBodyAsString();
        if (StringUtils.isEmpty(body))
        {
            throw new CredentialServiceClientException(errorMessage.toString(), clientException);
        }
        else
        {
            SecretStoreResponse response = null;
            try
            {
                response = JSONUtil.jsonToObject(body, SecretStoreResponse.class);
            }
            catch (Exception ex)
            {
                throw new CredentialServiceClientException(errorMessage.toString(), clientException);
            }
            throw new CredentialServiceClientException(response.getErrorCode(), clientException);
        }
    }
}
