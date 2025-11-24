package com.dtao.alien.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${app.mail.from}")
    private String senderEmail;

    @Value("${app.mail.name}")
    private String senderName;

    @Value("${app.mail.company}")
    private String companyName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        // 1. DEV MODE: Print OTP to Console (So you can login even if Email fails!)
//        System.out.println("\n==================================");
//        System.out.println("👉 DEV MODE OTP for " + toEmail + ": " + otp);
//        System.out.println("==================================\n");

        try {
            // 2. Prepare Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // 3. Prepare Body
            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            body.put("sender", sender);

            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            List<Map<String, String>> toList = Collections.singletonList(to);
            body.put("to", toList);

            body.put("subject", "Verify your " + companyName + " Account");

            String htmlContent = "<html><body>" +
                    "<h2>Welcome to " + companyName + "</h2>" +
                    "<p>Your verification code is:</p>" +
                    "<h1 style='color: #2563eb;'>" + otp + "</h1>" +
                    "</body></html>";

            body.put("htmlContent", htmlContent);

            // 4. Send Request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email sent to: " + toEmail);
            }

        } catch (Exception e) {
            // Graceful Error Handling (Don't crash, just warn)
            System.err.println("⚠️ Email sending failed (Invalid Key?): " + e.getMessage());
            System.err.println("👉 Use the manual OTP printed above to verify.");
        }
    }
}