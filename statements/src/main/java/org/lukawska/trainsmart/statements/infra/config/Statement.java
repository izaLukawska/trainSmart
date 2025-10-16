package org.lukawska.trainsmart.statements.infra.config;

public record Statement(String title, int version, boolean required, String content) {}