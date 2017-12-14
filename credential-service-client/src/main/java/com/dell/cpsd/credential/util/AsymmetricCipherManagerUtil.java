/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.util;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.exception.CipherManagerException;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
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
public class AsymmetricCipherManagerUtil
{
    public Object encryptCredentialElement(String publicKey, Object credentialElement) throws CredentialServiceClientException
    {
        //Create AsymmetricCipherManager
        AsymmetricCipherManager asymmetricCipherManager;
        try
        {
            asymmetricCipherManager = new AsymmetricCipherManager(Base64.getDecoder().decode(publicKey));
        }
        catch (CipherManagerException exception)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_PUBLIC_KEY_FORMAT.toString(), exception);
        }

        //Parse
        JsonNode input;
        try
        {
            input = JSONUtil.convertObjectToJsonNode(credentialElement);
        }
        catch (IOException ex)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_CREDENTIAL_ELEMENT_FORMAT.toString(), ex);
        }

        //Encrypt CredentialElement
        Iterator<Map.Entry<String, JsonNode>> iterator = input.fields();
        Map<String, String> response = new HashMap<>();
        String encryptedValue;
        while (iterator.hasNext())
        {
            Map.Entry<String, JsonNode> entry = iterator.next();
            try
            {
                encryptedValue= new String(asymmetricCipherManager.encrypt(entry.getValue().asText().getBytes()));
            }
            catch (CipherManagerException e)
            {
                throw new CredentialServiceClientException(ErrorMessages.ENCRYPTION_TRANSFORMATION_ERROR.toString(), e);
            }
            response.put(entry.getKey(), encryptedValue);
        }

        return response;
    }

}
