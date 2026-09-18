package com.naturaltaste.recommend.infrastructure.jwt;

import com.naturaltaste.recommend.application.port.TokenPort;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenPort implements TokenPort {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] secret;
    private final long expirationSeconds;

    public JwtTokenPort(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long expirationSeconds
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String createAccessToken(Long userId) {
        long expiresAt = Instant.now().plusSeconds(expirationSeconds).getEpochSecond();
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = base64Url(("{\"sub\":\"" + userId + "\",\"exp\":" + expiresAt + "}")
                .getBytes(StandardCharsets.UTF_8));
        String unsignedToken = header + "." + payload;

        return unsignedToken + "." + sign(unsignedToken);
    }

    @Override
    public Long parseUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
            throw new IllegalArgumentException("Invalid token signature");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        long expiresAt = Long.parseLong(readJsonValue(payload, "exp"));
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new IllegalArgumentException("Expired token");
        }

        return Long.valueOf(readJsonValue(payload, "sub"));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return base64Url(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign token", e);
        }
    }

    private String readJsonValue(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int keyStart = json.indexOf(keyPattern);
        if (keyStart < 0) {
            throw new IllegalArgumentException("Invalid token payload");
        }

        int valueStart = keyStart + keyPattern.length();
        if (json.charAt(valueStart) == '"') {
            int contentStart = valueStart + 1;
            int contentEnd = json.indexOf('"', contentStart);
            return json.substring(contentStart, contentEnd);
        }

        int valueEnd = json.indexOf(',', valueStart);
        if (valueEnd < 0) {
            valueEnd = json.indexOf('}', valueStart);
        }
        return json.substring(valueStart, valueEnd);
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }
}
