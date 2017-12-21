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
    /**
     * This method will return Public Key of Credential Service
     *
     * @return publicKey
     * @throws CredentialServiceClientException
     */
    String getPublicKey() throws CredentialServiceClientException;

    /**
     * This method will help to get Secret by Key.
     *
     * @param secretKey
     * @param publicKey
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getSecret(String secretKey, String publicKey) throws CredentialServiceClientException;

    /**
     * This method will help to get Secret by Key and Role.
     *
     * @param secretKey
     * @param role
     * @param publicKey
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getSecret(String secretKey, String role, String publicKey) throws CredentialServiceClientException;

    /**
     * This method will help to get Decrypted Secret by Key.
     *
     * @param secretKey
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getDecryptedSecret(String secretKey) throws CredentialServiceClientException;

    /**
     * This method will help to get Decrypted Secret by Key and Role.
     *
     * @param secretKey
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getDecryptedSecret(String secretKey, String role) throws CredentialServiceClientException;

    /**
     * This method will help to get Encrypted Secret by Secret Id.
     *
     * @param secretId
     * @param publicKey
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getSecret(Long secretId, String publicKey) throws CredentialServiceClientException;

    /**
     * This method will help to get Secret by Secret Id.
     *
     * @param secretId
     * @return
     * @throws CredentialServiceClientException
     */
    SecretStoreResponse getDecryptedSecret(Long secretId) throws CredentialServiceClientException;

    /**
     * This method will save Secret into Credential Service
     *
     * @param secretRequest
     * @return
     * @throws CredentialServiceClientException
     */
    String saveSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    /**
     * This method will Update Secret
     *
     * @param secretRequest
     * @return
     * @throws CredentialServiceClientException
     */
    String updateSecret(SecretRequest secretRequest) throws CredentialServiceClientException;

    /**
     * This method will delete both Secret and Key, if Secret is not referred by any other Key.
     * If Secret is referred by multiple Key's then only Key will be deleted not Secret
     *
     * @param secretKey
     * @throws CredentialServiceClientException
     */
    void deleteSecret(String secretKey) throws CredentialServiceClientException;

    /**
     * This method will delete Secret by Secret Id.
     * Secret will not be deleted if it is being referred by at least one Key.
     *
     * @param secretId
     * @throws CredentialServiceClientException
     */
    void deleteSecret(Long secretId) throws CredentialServiceClientException;

}
