package com.jct.mes_new.config.mail;

import com.jct.mes_new.biz.common.vo.MailConfigVo;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MailSenderFactory {
    private final ConcurrentHashMap<String, JavaMailSender> cache = new ConcurrentHashMap<>();

    public JavaMailSender getOrCreate(MailConfigVo acc) {
        return cache.computeIfAbsent(acc.getMailId(), k -> create(acc));
    }

    private JavaMailSender create(MailConfigVo acc) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(acc.getSmtpHost());
        sender.setPort(acc.getSmtpPort());
        sender.setUsername(acc.getSmtpUsername());
        sender.setPassword(acc.getSmtpPassword()); // 요구사항: 암복호화 없음

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.starttls.required", "false");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "true");

        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        props.put("mail.debug", "false");

        return sender;
    }

    public void evict(String mailId) {
        cache.remove(mailId);
    }
}
