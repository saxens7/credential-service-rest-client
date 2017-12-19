/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.client.rest;

import java.lang.reflect.Field;

/**
 * The is Helper class to inject Object to Private variables of a Class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class HelperUtility
{
    public static void accessPrivateFields(Class targetClass, Object objTarget, String injectFieldName, Object objInject)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        Field injectedProperty = targetClass.getDeclaredField(injectFieldName);
        injectedProperty.setAccessible(true);
        injectedProperty.set(objTarget, objInject);
    }
}
