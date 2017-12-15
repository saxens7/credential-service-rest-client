/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.credential.client.rest;

import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.request.SecretRequest;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;

/**
 * The credential manager service exception class.
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

    SecretStoreResponse getSecretByKey(String publicKey, String secretKey) throws CredentialServiceClientException;

    SecretStoreResponse getDecryptedSecretByKey(String secretKey) throws CredentialServiceClientException;

    SecretStoreResponse getSecretBySecretId(String publicKey, Long secretId) throws CredentialServiceClientException;

    SecretStoreResponse getDecryptedSecretBySecretId(Long secretId) throws CredentialServiceClientException;

    String saveSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    String updateSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    void deleteSecretByKey(String secretKey) throws CredentialServiceClientException;

    void deleteSecretBySecretId(Long secretId) throws CredentialServiceClientException;

}
