package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;

public interface MailSender {

    void sendEmail(MailRequest mailRequest) throws MessagingException;

}
