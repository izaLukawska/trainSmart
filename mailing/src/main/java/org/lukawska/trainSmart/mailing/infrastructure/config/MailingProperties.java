package org.lukawska.trainSmart.mailing.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mailing")
@Getter
@Setter
public class MailingProperties {

    private String from;

    private String replyTo;

    private Map<String, List<String>> mimeTypesByExt = new HashMap<>();

    private long maxSizeBytes = 0;

}
