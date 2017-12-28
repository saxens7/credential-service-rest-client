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
 * The AsymmetricCipherManager Utility class.
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

    @Autowired
    AsymmetricCipherManager asymmetricCipherManager;

    /**
     * This method will be used to build instance of AsymmetricCipherManager from provided public key
     *
     * @param publicKey
     * @return AsymmetricCipherManager
     * @throws CredentialServiceClientException
     */
    private AsymmetricCipherManager buildAsymmetricManager(String publicKey) throws CredentialServiceClientException
    {
        // Create AsymmetricCipherManager
        try
        {
            return new AsymmetricCipherManager(Base64.getDecoder().decode(publicKey));
        }
        catch (CipherManagerException exception)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_PUBLIC_KEY_FORMAT.toString(), exception);
        }
    }

    /**
     * This method will convert Object to Json Node.
     * 
     * @param credentialElement
     * @return JsonNode
     * @throws CredentialServiceClientException
     */
    private JsonNode convertObjectToJsonNode(Object credentialElement) throws CredentialServiceClientException
    {
        // Parse
        try
        {
            return JsonUtil.convertObjectToJsonNode(credentialElement);
        }
        catch (IOException ex)
        {
            throw new CredentialServiceClientException(ErrorMessages.INVALID_CREDENTIAL_ELEMENT_FORMAT.toString(), ex);
        }
    }

    /**
     * This method will Encrypt or Decrypt Credential Element depending upon boolean value "encrypt"
     * 
     * @param asymmetricCipherManager
     * @param jsonNode
     * @param encrypt
     * @return Map<String, String>
     * @throws CipherManagerException
     */
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

    /**
     * This method will decrypt credentialElement.
     * 
     * @param credentialElement
     * @return Object
     * @throws CredentialServiceClientException
     */
    public Object decryptCredentialElement(Object credentialElement) throws CredentialServiceClientException
    {
        // Parse
        JsonNode input = convertObjectToJsonNode(credentialElement);

        // Encrypt CredentialElement
        try
        {
            return encryptOrDecryptCredentialElement(asymmetricCipherManager, input, false);
        }
        catch (CipherManagerException e)
        {
            throw new CredentialServiceClientException(ErrorMessages.DECRYPTION_TRANSFORMATION_ERROR.toString(), e);
        }
    }

    /**
     * This method will encrypt credentialElement.
     * 
     * @param publicKey
     * @param credentialElement
     * @return Object
     * @throws CredentialServiceClientException
     */
    public Object encryptCredentialElement(String publicKey, Object credentialElement) throws CredentialServiceClientException
    {
        // Get AsymmetricCipherManager
        AsymmetricCipherManager asymmetricCipherManager = buildAsymmetricManager(publicKey);

        // Parse
        JsonNode input = convertObjectToJsonNode(credentialElement);

        // Encrypt CredentialElement
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
