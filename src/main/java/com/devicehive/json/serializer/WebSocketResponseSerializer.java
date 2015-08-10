package com.devicehive.json.serializer;

import com.devicehive.json.domain.*;
import com.devicehive.json.domain.ErrorDescription;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

//fixme need to find another way to have different names for com.devicehive.json.domain.WebSocketResponse#handlerResponse property
public class WebSocketResponseSerializer extends JsonSerializer<WebSocketResponse> {

    @Override
    public void serialize(WebSocketResponse value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        if (value.getAction().isPresent())
            jgen.writeStringField("action", value.getAction().get());

        jgen.writeStringField("status", value.getStatus());

        if (value.getRequestId().isPresent())
            jgen.writeStringField("requestId", value.getRequestId().get());

        Optional<ErrorDescription> errOpt = value.getError();
        if (errOpt.isPresent()) {
            jgen.writeNumberField("code", errOpt.map(ErrorDescription::getErrorCode).orElse(null));
            jgen.writeStringField("error", errOpt.map(ErrorDescription::getErrorMessage).orElse(null));
        }

        if (value.getHandlerResponse() != null) {
            Optional<JsonRootName> rootName = Optional.ofNullable(value.getHandlerResponse().getClass().getAnnotation(JsonRootName.class));
            String fieldName = rootName.map(JsonRootName::value).orElse("response");
            jgen.writeObjectField(fieldName, value.getHandlerResponse());
        }

        jgen.writeEndObject();
    }

}
