/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.credential.client.rest;

import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;

/**
 * The credential service interface.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface CredentialServiceClient
{
    String getPublicKey() throws CredentialServiceClientException;

    SecretStoreResponse getSecret(String secretKey, String publicKey) throws CredentialServiceClientException;

    SecretStoreResponse getSecret(String secretKey) throws CredentialServiceClientException;

    SecretStoreResponse getSecret(String publicKey, Long secretId) throws CredentialServiceClientException;

    SecretStoreResponse getSecret(Long secretId) throws CredentialServiceClientException;

    String saveSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    String updateSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    void deleteSecret(String secretKey) throws CredentialServiceClientException;

    void deleteSecret(Long secretId) throws CredentialServiceClientException;

}
