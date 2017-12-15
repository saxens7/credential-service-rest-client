/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.credential.util;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.exception.CipherManagerException;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import com.fasterxml.jackson.databind.JsonNode;
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

    private AsymmetricCipherManager buildAsymmetricManager(String publicKey) throws CredentialServiceClientException
    {
        //Create AsymmetricCipherManager
        try
        {
            return new AsymmetricCipherManager(Base64.getDecoder().decode(publicKey));
        }
        catch (CipherManagerException exception)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_PUBLIC_KEY_FORMAT.toString(), exception);
        }
    }

    private JsonNode convertObjectToJsonNode(Object credentialElement) throws CredentialServiceClientException
    {
        //Parse
        try
        {
            return JSONUtil.convertObjectToJsonNode(credentialElement);
        }
        catch (IOException ex)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_CREDENTIAL_ELEMENT_FORMAT.toString(), ex);
        }
    }

    private Map<String, String> encryptOrDecryptCredentialElement(AsymmetricCipherManager asymmetricCipherManager, JsonNode jsonNode,
            Boolean encrypt) throws CipherManagerException
    {
        Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
        Map<String, String> response = new HashMap<>();
        String value;
        while (iterator.hasNext())
        {
            Map.Entry<String, JsonNode> entry = iterator.next();
            if (encrypt)
            {
                value = new String(asymmetricCipherManager.encrypt(entry.getValue().asText().getBytes()));
            }
            else
            {
                value = new String(asymmetricCipherManager.decrypt(entry.getValue().asText().getBytes()));
            }
            response.put(entry.getKey(), value);
        }
        return response;
    }

    public Object decryptCredentialElement(Object credentialElement, AsymmetricCipherManager asymmetricCipherManager) throws CredentialServiceClientException
    {
        //Parse
        JsonNode input = convertObjectToJsonNode(credentialElement);

        //Encrypt CredentialElement
        try
        {
            return encryptOrDecryptCredentialElement(asymmetricCipherManager, input, false);
        }
        catch (CipherManagerException e)
        {
            throw new CredentialServiceClientException(ErrorMessages.DECRYPTION_TRANSFORMATION_ERROR.toString(), e);
        }
    }

    public Object encryptCredentialElement(String publicKey, Object credentialElement) throws CredentialServiceClientException
    {
        //Get AsymmetricCipherManager
        AsymmetricCipherManager asymmetricCipherManager = buildAsymmetricManager(publicKey);

        //Parse
        JsonNode input = convertObjectToJsonNode(credentialElement);

        //Encrypt CredentialElement
        try
        {
            return encryptOrDecryptCredentialElement(asymmetricCipherManager, input, true);
        }
        catch (CipherManagerException e)
        {
            throw new CredentialServiceClientException(ErrorMessages.ENCRYPTION_TRANSFORMATION_ERROR.toString(), e);
        }
    }

}
