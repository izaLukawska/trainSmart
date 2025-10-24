package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;

public interface MailSender {

    void sendEmail(MailRequest mailRequest) throws MessagingException;

}
