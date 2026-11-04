package com.esporteclubepelotas.api.common.email;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlContent);
}

