package org.lukawska.trainsmart.mailing.infrastructure.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukawska.trainsmart.mailing.testdata.MailingTestData.mailRequestWithAttachments;

@Testcontainers
@SpringBootTest
public class MailSenderAdapterIT extends PostgresTestBase {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog:latest").withExposedPorts(1025, 8025);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.mail.protocol", () -> "smtp");
    }

    @Autowired
    private MailSenderAdapter mailSenderAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendMailSuccess() throws Exception {
        //when
        MailRequest mailRequest = mailRequestWithAttachments();
        mailSenderAdapter.sendEmail(mailRequest);
        URI uri = URI.create(String.format("http://%s:%d/api/v2/messages",
                                           mailhog.getHost(), mailhog.getMappedPort(8025)));

        HttpRequest httpRequest = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(httpResponse.body());
        JsonNode items = root.path("items");
        assertFalse(items.isEmpty(), "No messages in MailHog");

        JsonNode first = items.get(0);
        JsonNode subjectNode = first.path("Content").path("Headers").path("Subject");
        String subject = subjectNode.isArray() && !subjectNode.isEmpty() ? subjectNode.get(0).asText() : "";

        assertEquals(mailRequest.subject(), subject);
    }

    @Test
    void shouldThrowMailSendErrorAfterAllRetriesFailed() {
        //given
        mailhog.stop();

        //when && then
        assertThrows(MailException.class, () -> mailSenderAdapter.sendEmail(mailRequestWithAttachments()));
    }
}
