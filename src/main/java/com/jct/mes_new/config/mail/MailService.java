package com.jct.mes_new.config.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.activation.DataSource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MailService {

    public enum BodyType { FTL, HTML_FILE, RAW_HTML, RAW_TEXT }

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    @Value("${app.mail.from:purchase@jincostech.com}")
    private String from;

    @Value("${app.mail.fromName:}")
    private String fromName;

    /**
     * ✅ 사용자가 원하신 시그니처 그대로 (FTL + 첨부(Map<DataSource>))
     * templateName: "poSheetMail.ftl"
     * model: FreeMarker model (예: "vo" -> poSheetMailVo 또는 "poSheetMailVo" -> ...)
     */
    public void send(String to,
                     String cc,
                     String subject,
                     Map<String, DataSource> files,
                     String templateName,
                     Map<String, Object> model) {
        internalSend(to, cc, subject, BodyType.FTL, templateName, model, files, null);
    }

    /**
     * ✅ 비동기 버전 (원하면 사용)
     * 사용하려면 @EnableAsync 필요
     */
    @Async
    public void sendAsync(String to,
                          String cc,
                          String subject,
                          Map<String, DataSource> files,
                          String templateName,
                          Map<String, Object> model) {
        internalSend(to, cc, subject, BodyType.FTL, templateName, model, files, null);
    }

    /**
     * ✅ 정적/간단 HTML 파일(classpath) 보내기
     * htmlPath 예: "mail/static_notice.html"
     * 기본은 치환 없이 그대로 발송.
     * (원하면 {{key}} 치환도 지원하도록 아래에서 처리)
     */
    public void sendHtmlFile(String to,
                             String cc,
                             String subject,
                             String htmlPath,
                             Map<String, Object> replaceModel,
                             Map<String, DataSource> files) {
        internalSend(to, cc, subject, BodyType.HTML_FILE, htmlPath, replaceModel, files, null);
    }

    /**
     * ✅ HTML 문자열을 그대로 보낼 때
     */
    public void sendRawHtml(String to,
                            String cc,
                            String subject,
                            String html,
                            Map<String, DataSource> files) {
        internalSend(to, cc, subject, BodyType.RAW_HTML, html, null, files, null);
    }

    /**
     * ✅ 텍스트 메일
     */
    public void sendRawText(String to,
                            String cc,
                            String subject,
                            String text,
                            Map<String, DataSource> files) {
        internalSend(to, cc, subject, BodyType.RAW_TEXT, text, null, files, null);
    }

    /**
     * ✅ (선택) MultipartFile 첨부를 쓰는 경우를 위해 오버로드 제공
     */
    public void sendFtlWithMultipart(String to,
                                     String cc,
                                     String subject,
                                     String templateName,
                                     Map<String, Object> model,
                                     List<MultipartFile> multipartFiles) {
        internalSend(to, cc, subject, BodyType.FTL, templateName, model, null, multipartFiles);
    }

    // =========================
    // Internal
    // =========================
    private void internalSend(String to,
                              String cc,
                              String subject,
                              BodyType bodyType,
                              String bodySource,
                              Map<String, Object> model,
                              Map<String, DataSource> files,
                              List<MultipartFile> multipartFiles) {

        try {
            String body = switch (bodyType) {
                case FTL -> renderFtl(bodySource, model);
                case HTML_FILE -> loadHtmlFromClasspath(bodySource, model);
                case RAW_HTML, RAW_TEXT -> bodySource; // bodySource 자체가 본문
            };

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // From
            if (fromName != null && !fromName.isBlank()) {
                helper.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            } else {
                helper.setFrom(from);
            }

            // To / Cc
            helper.setTo(splitEmails(to));
            String[] ccArr = splitEmails(cc);
            if (ccArr.length > 0) helper.setCc(ccArr);

            helper.setSubject(subject);

            // Body
            boolean isHtml = (bodyType != BodyType.RAW_TEXT);
            helper.setText(body, isHtml);

            // Attachments: DataSource map
            if (files != null && !files.isEmpty()) {
                for (Map.Entry<String, DataSource> e : files.entrySet()) {
                    String fileName = e.getKey();
                    DataSource ds = e.getValue();
                    if (isBlank(fileName) || ds == null) continue;
                    helper.addAttachment(fileName, ds);
                }
            }

            // Attachments: Multipart
            if (multipartFiles != null && !multipartFiles.isEmpty()) {
                for (MultipartFile f : multipartFiles) {
                    if (f == null || f.isEmpty()) continue;
                    String filename = Optional.ofNullable(f.getOriginalFilename()).orElse("attachment");
                    helper.addAttachment(filename, f::getInputStream, f.getContentType());
                }
            }

            mailSender.send(message);

        } catch (Exception ex) {
            throw new RuntimeException("메일 발송 실패: " + ex.getMessage(), ex);
        }
    }

    /**
     * FreeMarker 렌더링
     * - templateName 예: "poSheetMail.ftl" 또는 "mail/poSheetMail.ftl"
     * - model에서 템플릿이 ${vo.xxx} 를 쓰는 경우를 위해 보정 포함
     */
    private String renderFtl(String templateName, Map<String, Object> model) throws Exception {
        Map<String, Object> safeModel = (model != null) ? new HashMap<>(model) : new HashMap<>();

        // ✅ 템플릿이 ${vo.xxx}인데 호출자가 poSheetMailVo로만 넣는 경우 자동 보정
        if (!safeModel.containsKey("vo") && safeModel.containsKey("poSheetMailVo")) {
            safeModel.put("vo", safeModel.get("poSheetMailVo"));
        }

        Template template = freemarkerConfig.getTemplate(templateName, StandardCharsets.UTF_8.name());

        try (StringWriter out = new StringWriter()) {
            template.process(safeModel, out);
            return out.toString();
        }
    }

    /**
     * classpath HTML 파일 읽기
     * - 기본: 그대로 전송
     * - (선택) replaceModel이 있으면 {{key}} 형태를 단순 치환
     */
    private String loadHtmlFromClasspath(String htmlPath, Map<String, Object> replaceModel) throws Exception {
        // templates 폴더 아래에 html을 둘 거면 "templates/" 경로 붙여 읽기
        // 예: src/main/resources/templates/mail/static_notice.html
        ClassPathResource res = new ClassPathResource("templates/" + htmlPath);

        String html = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);

        // 단순 치환 옵션: {{customerName}} 같은 토큰만 처리
        if (replaceModel != null && !replaceModel.isEmpty()) {
            for (Map.Entry<String, Object> e : replaceModel.entrySet()) {
                String key = e.getKey();
                String val = e.getValue() == null ? "" : String.valueOf(e.getValue());
                html = html.replace("{{" + key + "}}", val);
            }
        }
        return html;
    }

    private String[] splitEmails(String emails) {
        if (emails == null) return new String[0];
        String s = emails.trim();
        if (s.isEmpty()) return new String[0];

        // 콤마/세미콜론 모두 지원
        s = s.replace(";", ",");
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .toArray(String[]::new);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
