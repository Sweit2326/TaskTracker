package dev.mironov.backendservice.record;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
    String title,
    String content,
    String status,
    String completed_at
) {}
