/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. DELL EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.credential.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is test class for JsonUtil.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonUtilTest
{
    Map<String, String> map;
    String              json;

    @Before
    public void setup()
    {
        map = new HashMap<>();
        map.put("abc", "ABC");
        map.put("xyz", "XYZ");
        json = "{\"abc\":\"ABC\",\"xyz\":\"XYZ\"}";

    }

    @After
    public void destroy()
    {
        map = null;
        json = null;
    }

    @Test
    public void testJsonToObject() throws IOException
    {
        String json = JsonUtil.convertObjectToJson(map);

        Map<String, String> mapResponse = JsonUtil.jsonToObject(json, Map.class);

        Assert.assertEquals(map.get("abc"), mapResponse.get("abc"));
        Assert.assertEquals(map.get("xyz"), mapResponse.get("xyz"));
    }

    @Test(expected = IOException.class)
    public void testJsonToObjectException() throws IOException
    {
        Map<String, String> mapResponse = JsonUtil.jsonToObject("abc", Map.class);
    }

    @Test
    public void testConvertObjectToJson() throws IOException
    {
        String result = JsonUtil.convertObjectToJson(map);
        Assert.assertEquals(result, json);
    }

    @Test(expected = IOException.class)
    public void testConvertObjectToJsonException() throws IOException
    {
        JsonUtil.convertObjectToJson(new Object());
    }

    @Test
    public void testConvertObjectToJsonNode() throws IOException
    {
        JsonNode result = JsonUtil.convertObjectToJsonNode(map);
        Assert.assertEquals(json, result.toString());
    }

    @Test(expected = IOException.class)
    public void testConvertObjectToJsonNodeException() throws IOException
    {
        JsonUtil.convertObjectToJsonNode(new Object());
    }
}
