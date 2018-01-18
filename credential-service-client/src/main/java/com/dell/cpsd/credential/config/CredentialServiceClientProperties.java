/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.config;

/**
 * The credential service Configuration properties.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class CredentialServiceClientProperties
{
    private String hostName;
    private String port;

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(final String hostName)
    {
        this.hostName = hostName;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(final String port)
    {
        this.port = port;
    }
}
