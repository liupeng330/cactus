/*
 * Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author zixin.wang created on 2013 13-8-16 下午7:35
 * @version 1.0.0
 */
public class JsonDataTypeSerializer extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }
}
