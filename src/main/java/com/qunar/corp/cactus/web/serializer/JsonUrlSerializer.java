/*
 * Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.serializer;

import com.alibaba.dubbo.common.URL;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * @author zixin.wang created on 2013 13-8-16 下午7:35
 * @version 1.0.0
 */
public class JsonUrlSerializer extends JsonSerializer<URL> {

    @Override
    public void serialize(URL value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toFullString());
    }
}
