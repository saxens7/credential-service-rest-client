/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
public class JsonUtil
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonToObject(String json, Class<T> classType) throws IOException
    {
        return objectMapper.readValue(json, classType);
    }

    public static String convertObjectToJson(Object object) throws IOException
    {
        return objectMapper.writeValueAsString(object);
    }

    public static JsonNode convertObjectToJsonNode(Object object) throws IOException
    {
        String json = convertObjectToJson(object);
        return jsonToObject(json, JsonNode.class);
    }
}
