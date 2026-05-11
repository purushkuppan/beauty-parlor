package com.beautyparlour.model.dto;

import java.util.Map;

public record ValidationErrorResponse(String timestamp, int status, String error, Map<String, String> fields) {}
