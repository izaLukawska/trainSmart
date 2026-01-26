package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;

public interface MailSender {

    void sendEmail(MailDetails mailDetails) throws MessagingException;

}
