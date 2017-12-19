/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.util;

import com.dell.cpsd.common.keystore.encryption.AsymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.SymmetricCipherManager;
import com.dell.cpsd.common.keystore.encryption.exception.CipherManagerException;
import com.dell.cpsd.credential.client.rest.HelperUtility;
import com.dell.cpsd.credential.exception.CredentialServiceClientException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * The AsymmetricCipherManagerUtil Test.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AsymmetricCipherManagerUtilTest
{
    @InjectMocks
    AsymmetricCipherManagerUtil asymmetricCipherManagerUtil;

    @Mock
    AsymmetricCipherManager asymmetricCipherManager;

    String              publickKey;
    String              incompatiblePublickKey;
    String              invalidPublickKey;
    Map<String, String> credentialElement;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException, CipherManagerException
    {
        asymmetricCipherManager = new AsymmetricCipherManager();
        asymmetricCipherManager.setSymmetricCipherManager(new SymmetricCipherManager());
        asymmetricCipherManager.initialize();
        HelperUtility.accessPrivateFields(AsymmetricCipherManagerUtil.class,
                asymmetricCipherManagerUtil, "asymmetricCipherManager", asymmetricCipherManager);

        publickKey = asymmetricCipherManager.getPublicKeyEncodedBase64();
        incompatiblePublickKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6fje7eDj6GhuiGrIRDQek3C/LYLKk69OLqHTBfgJspKO5IEEZsZLCFQOi6BughtZlYS5SsDWqTZ/jB0KHICxHDqgjSyd4xMQVDG8ARxbTufN3OyL0k0GK5a6L/wJjgu1Inx2MHofkRuIemM9/JxepmY6l9zsZPIJZYrQ0iJ4+QZ0ggwQNKnUT5UNVrkTIYsi/ZckFeB/6mYLqOXmA1w0OBCi1EvQN7V5ixWNcj2Kdx3xu6OY4By2afEb0eXe6u05RfU0R3UvQPmu15nrXNpPTLe5yr0EJOcnjKZpM39bohn8hkeopwO1nRSNOXLPnbyHHne0MpWQuwjmN2UX/HBP4QIDAQAB";
        invalidPublickKey = "dgfasdjghjkzdhbvgjkadfhk";
        credentialElement = new HashMap<>();
        credentialElement.put("user", "ABC");
        credentialElement.put("pwd", "XYZ");
    }

    @Test
    public void testEncryptCredentialElement() throws CredentialServiceClientException
    {
        Map<String, String> encrypted = (Map<String, String>) asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, credentialElement);
        Map<String, String> decrypted = (Map<String, String>)asymmetricCipherManagerUtil.decryptCredentialElement(encrypted);

        Assert.assertEquals(credentialElement.get("user"), decrypted.get("user"));
        Assert.assertEquals(credentialElement.get("pwd"), decrypted.get("pwd"));
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testEncryptCredentialElementForIncompatiblePublicKey() throws CredentialServiceClientException
    {
        Map<String, String> encrypted = (Map<String, String>) asymmetricCipherManagerUtil.encryptCredentialElement(incompatiblePublickKey, credentialElement);
        Map<String, String> decrypted = (Map<String, String>)asymmetricCipherManagerUtil.decryptCredentialElement(encrypted);
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testEncryptCredentialElementForInvalidPublicKey() throws CredentialServiceClientException
    {
        asymmetricCipherManagerUtil.encryptCredentialElement(invalidPublickKey, credentialElement);
    }

    @Test
    public void testDecryptCredentialElement() throws CredentialServiceClientException
    {
        Map<String, String> encrypted = (Map<String, String>) asymmetricCipherManagerUtil.encryptCredentialElement(publickKey, credentialElement);
        Map<String, String> decrypted = (Map<String, String>)asymmetricCipherManagerUtil.decryptCredentialElement(encrypted);

        Assert.assertEquals(credentialElement.get("user"), decrypted.get("user"));
        Assert.assertEquals(credentialElement.get("pwd"), decrypted.get("pwd"));
    }

    @Test(expected = CredentialServiceClientException.class)
    public void testDecryptCredentialElementForIncompatiblePublicKey() throws CredentialServiceClientException
    {
        Map<String, String> encrypted = (Map<String, String>) asymmetricCipherManagerUtil.encryptCredentialElement(incompatiblePublickKey, credentialElement);
        Map<String, String> decrypted = (Map<String, String>)asymmetricCipherManagerUtil.decryptCredentialElement(encrypted);
    }

}
