package org.lukawska.trainsmart.mailing.infra.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.mailing.infrastructure.external.MailSenderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testutil.MailingTestData.mailDetailsWithAttachments;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresTestConfig.class)
class MailSenderAdapterIT {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog:latest")
            .withExposedPorts(1025, 8025);

    @Autowired
    private MailingProperties mailingProperties;

    @Autowired
    private MailSenderAdapter mailSenderAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
    }

    @Test
    void shouldSendMailSuccess() throws Exception {
        //given
        final MailDetails mailRequest = mailDetailsWithAttachments();

        //when
        mailSenderAdapter.sendEmail(mailRequest);

        //then
        URI uri = URI.create(String.format("http://%s:%d/api/v2/messages",
                                           mailhog.getHost(), mailhog.getMappedPort(8025)));

        HttpRequest httpRequest = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(httpResponse.statusCode()).isEqualTo(200);

        JsonNode sentMail = objectMapper.readTree(httpResponse.body()).at("/items/0");
        assertThat(sentMail.isMissingNode()).isFalse();

        String subject = getHeaderValue(sentMail, "Subject");
        String from = getHeaderValue(sentMail, "From");
        String replyTo = getHeaderValue(sentMail, "Reply-To");

        assertThat(subject).isEqualTo(mailRequest.subject());
        assertThat(replyTo).isEqualTo(mailingProperties.getReplyTo());
        assertThat(from).isEqualTo(mailingProperties.getFrom());
    }

    @Test
    @DirtiesContext
    void shouldThrowMailSendErrorAfterAllRetriesFailed() {
        //given
        mailhog.stop();

        //when && then
        assertThatThrownBy(() -> mailSenderAdapter.sendEmail(mailDetailsWithAttachments()))
                .isInstanceOf(MailException.class);
    }

    private String getHeaderValue(JsonNode mailNode, String headerName) {
        JsonNode node = mailNode.at("/Content/Headers/" + headerName);

        return node.isArray() && !node.isEmpty() ? node.get(0).asText() : "";
    }
}
