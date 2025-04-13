package com.example.demo.user.service;

import com.example.demo.config.MailConfig;
import com.example.demo.util.EnvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender = MailConfig.javaMailSender();

    public void sendPasswordResetEmail(String recipientEmail, String resetLink) {
        String subject = "비밀번호 재설정 안내";
        String htmlContent = buildResetEmailBody(resetLink);

        try {
            MimeMessage message = createHtmlMessage(recipientEmail, subject, htmlContent);
            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Error sending email to {}: {}", recipientEmail, e.getMessage(), e);
            throw new IllegalStateException("비밀번호 재설정 이메일 전송 실패", e);
        }
    }

    private String buildResetEmailBody(String resetLink) {
        return new StringBuilder()
                .append("<p>아래 링크를 클릭하여 비밀번호를 재설정하세요:</p>")
                .append("<a href=\"").append(resetLink).append("\">비밀번호 재설정 링크</a>")
                .toString();
    }

    private MimeMessage createHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setFrom(EnvUtil.get("MAIL_USERNAME"));
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        return message;
    }
}
