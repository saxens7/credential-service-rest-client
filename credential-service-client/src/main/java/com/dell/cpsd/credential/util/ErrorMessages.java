/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.util;

import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.dell.cpsd.credential.model.rest.api.response.SecretStoreResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

/**
 * The credential manager service exception class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public enum ErrorMessages
{
    NULL_POST_REQUEST_PAYLOAD("Save secret request body can't be null."),
    SAVE_SECRET_FAILED("Save Secret Failed."),
    UPDATE_SECRET_FAILED("Update Secret Failed."),
    DELETE_SECRET_BY_KEY_FAILED("Delete Secret by key Failed."),
    DELETE_SECRET_BY_SECRET_ID_FAILED("Delete Secret by secretId Failed."),
    INVALID_PUBLIC_KEY_FORMAT("Invalid Public key format."),
    INVALID_CREDENTIAL_ELEMENT_FORMAT("Invalid format for credential element."),
    ENCRYPTION_TRANSFORMATION_ERROR("Error encrypting the Credential Element."),
    DECRYPTION_TRANSFORMATION_ERROR("Error decrypting the Credential Element."),
    GET_PUBLIC_KEY_FAILED("Get Public key Failed."),
    GET_SECRET_BY_KEY_FAILED("Get Secret by key Failed."),
    GET_SECRET_BY_SECRET_ID_FAILED("Get Secret by key secretId Failed.");

    private String errorMessage = "Action Failed!";

    ErrorMessages(String msg)
    {
        this.errorMessage = msg;
    }

    public void processException(RestClientException clientException) throws CredentialServiceClientException
    {
        String body = ((HttpClientErrorException) clientException).getResponseBodyAsString();
        if (StringUtils.isEmpty(body))
        {
            throw new CredentialServiceClientException(this.errorMessage, clientException);
        }
        else
        {
            SecretStoreResponse response = null;
            try
            {
                response = new JSONUtil().jsonToObject(body, SecretStoreResponse.class);
            }
            catch (Exception ex)
            {
                throw new CredentialServiceClientException(this.errorMessage, clientException);
            }
            throw new CredentialServiceClientException(response.getError(), clientException);
        }
    }

    @Override
    public String toString()
    {
        return this.errorMessage;
    }
}
