/*
 * Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author zixin.wang created on 2013 13-8-16 下午7:35
 * @version 1.0.0
 */
public class JsonDateDeSerializer extends JsonDeserializer<Timestamp> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        try {
            return Timestamp.valueOf(node.getTextValue());
        } catch (RuntimeException e) {
            logger.error("json format error", e);
        }
        return null;
    }
}
