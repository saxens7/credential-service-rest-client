/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.exception;

/**
 * The credential service client exception class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class CredentialServiceClientException extends Exception
{
    public CredentialServiceClientException(final String message)
    {
        super(message);
    }

    public CredentialServiceClientException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
