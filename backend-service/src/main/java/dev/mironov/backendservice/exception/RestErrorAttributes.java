package dev.mironov.backendservice.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webReq, ErrorAttributeOptions options) {
        Map<String, Object> defaultAttributes = super.getErrorAttributes(webReq, options);
        String error = (String) defaultAttributes.getOrDefault("error", "Internal Server Error");

        Map<String, Object> customAttributes = new LinkedHashMap<>();
        customAttributes.put("message", error);

        return customAttributes;
    }
}
