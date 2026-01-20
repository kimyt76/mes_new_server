package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.config.common.exception.MailSendException;
import com.jct.mes_new.biz.common.mapper.MailConfigMapper;
import com.jct.mes_new.biz.common.service.MailService;
import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.config.mail.FreemarkerBodyRenderer;
import com.jct.mes_new.config.mail.MailSenderFactory;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
    private final MailConfigMapper mailConfigMapper;
    private final MailSenderFactory mailSenderFactory;
    private final FreemarkerBodyRenderer freemarkerBodyRenderer;

    @Override
    public String sendMail(MailVo vo, Map<String, DataSource> files) {
        return sendMail(vo, files, Map.of());
    }

    @Override
    public String sendMail(MailVo vo, Map<String, DataSource> files, Map<String, Object> model) {
        Objects.requireNonNull(vo, "vo is null");

        // 0) 계정 조회/메일 sender 준비
        var acc = mailConfigMapper.selectMailConfig(vo.getMailId());
        if (acc == null) {
            throw new MailSendException("메일 계정 설정을 찾을 수 없습니다. (mailId=" + vo.getMailId() + ")");
        }

        final JavaMailSender mailSender;
        try {
            mailSender = mailSenderFactory.getOrCreate(acc);
        } catch (Exception e) {
            log.error("[MAIL_CONFIG] mail sender create failed", e);
            throw new MailSendException("메일 발송 설정 초기화 단계에서 오류가 발생했습니다.", e);
        }

        final MimeMessage message;
        final MimeMessageHelper helper;
        try {
            message = mailSender.createMimeMessage();
            helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("[MAIL_INIT] create message/helper failed", e);
            throw new MailSendException("메일 초기화 단계에서 오류가 발생했습니다.", e);
        }

        // 1) 주소/헤더 세팅
        try {
            // To
            if (isBlank(vo.getTo())) {
                throw new MailSendException("수신자(To) 이메일이 비어있습니다.");
            }
            helper.setTo(vo.getTo().trim());

            // Cc / Bcc (빈 문자열은 set 금지)
            if (!isBlank(vo.getCc())) helper.setCc(vo.getCc().trim());
            if (!isBlank(vo.getBcc())) helper.setBcc(vo.getBcc().trim());

            // From: DB 우선
            if (!isBlank(acc.getFromEmail())) {
                if (!isBlank(acc.getFromName())) helper.setFrom(acc.getFromEmail().trim(), acc.getFromName().trim());
                else helper.setFrom(acc.getFromEmail().trim());
            } else if (!isBlank(vo.getFrom())) {
                helper.setFrom(vo.getFrom().trim());
            } else {
                throw new MailSendException("발신자(From) 이메일이 설정되지 않았습니다.");
            }

            if (isBlank(vo.getSubject())) {
                throw new MailSendException("메일 제목(subject)이 비어있습니다.");
            }
            helper.setSubject(vo.getSubject());

        } catch (MailSendException e) {
            // 우리가 의도적으로 던진 메시지는 그대로 올림
            throw e;
        } catch (Exception e) {
            log.error("[MAIL_ADDRESS] address/header set failed", e);
            throw new MailSendException("메일 주소(수신자/참조/발신자) 설정 단계에서 오류가 발생했습니다.", e);
        }

        // 2) 본문 렌더링/세팅
        try {
            if (!isBlank(vo.getTemplatePath())) {
                String html = freemarkerBodyRenderer.render(vo.getTemplatePath(), model);
                helper.setText(html, true);
            } else {
                // 템플릿이 없으면 content 사용
                String content = vo.getContent();
                if (content == null) content = "";
                helper.setText(content, vo.isHtml());
            }
        } catch (Exception e) {
            log.error("[MAIL_BODY] body render/set failed", e);
            throw new MailSendException("메일 본문(템플릿/내용) 생성 단계에서 오류가 발생했습니다.", e);
        }

        // 3) 첨부 처리
        try {
            Map<String, DataSource> safeFiles = normalizeAttachments(files);
            for (Map.Entry<String, DataSource> ent : safeFiles.entrySet()) {
                if (ent.getValue() == null) continue;
                helper.addAttachment(ent.getKey(), ent.getValue());
            }
        } catch (Exception e) {
            log.error("[MAIL_ATTACH] attachment add failed", e);
            throw new MailSendException("메일 첨부파일 처리 단계에서 오류가 발생했습니다.", e);
        }

        // 4) 실제 발송
        try {
            mailSender.send(message);
            return "메일이 발송되었습니다.";
        } catch (Exception e) {
            log.error("[MAIL_SEND] mail send failed", e);
            throw new MailSendException("메일 발송(SMTP 전송) 단계에서 오류가 발생했습니다.", e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Map<String, DataSource> normalizeAttachments(Map<String, DataSource> files) {
        Map<String, DataSource> result = new LinkedHashMap<>();
        if (files == null || files.isEmpty()) return result;

        for (Map.Entry<String, DataSource> e : files.entrySet()) {
            if (e == null || e.getValue() == null) continue;

            String rawName = e.getKey();
            String fileName = (rawName == null || rawName.isBlank()) ? "attachment" : rawName.trim();
            fileName = ensureUniqueFileName(result, fileName);
            result.put(fileName, e.getValue());
        }
        return result;
    }

    private String ensureUniqueFileName(Map<String, DataSource> existing, String fileName) {
        if (!existing.containsKey(fileName)) return fileName;

        String base;
        String ext;

        int dot = fileName.lastIndexOf('.');
        if (dot > 0 && dot < fileName.length() - 1) {
            base = fileName.substring(0, dot);
            ext = fileName.substring(dot);
        } else {
            base = fileName;
            ext = "";
        }

        int i = 2;
        String candidate;
        do {
            candidate = base + "(" + i++ + ")" + ext;
        } while (existing.containsKey(candidate));

        return candidate;
    }
}
