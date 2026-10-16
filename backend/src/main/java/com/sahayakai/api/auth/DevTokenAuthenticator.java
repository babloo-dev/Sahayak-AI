package com.sahayakai.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/** Local-only token authentication. Production authentication requires a real identity provider. */
@Component
public class DevTokenAuthenticator {

    private final boolean enabled;
    private final List<TokenIdentity> identities;

    public DevTokenAuthenticator(
            Environment environment,
            @Value("${sahayak.auth.dev-token.enabled:false}") boolean enabled,
            @Value("${SAHAYAK_DEV_CITIZEN_TOKEN:}") String citizenToken,
            @Value("${SAHAYAK_DEV_CITIZEN_SUBJECT:local-citizen}") String citizenSubject,
            @Value("${SAHAYAK_DEV_RESCUE_TOKEN:}") String rescueToken,
            @Value("${SAHAYAK_DEV_RESCUE_TEAM:Local response team}") String rescueTeam,
            @Value("${SAHAYAK_DEV_ADMIN_TOKEN:}") String adminToken,
            @Value("${SAHAYAK_DEV_ADMIN_SUBJECT:local-admin}") String adminSubject) {
        this.enabled = enabled && environment.acceptsProfiles(Profiles.of("local"));
        this.identities = new ArrayList<>();
        addIdentity(citizenToken, citizenSubject, ApiRole.CITIZEN);
        addIdentity(rescueToken, rescueTeam, ApiRole.RESCUE_TEAM);
        addIdentity(adminToken, adminSubject, ApiRole.ADMIN);
    }

    public Optional<AuthenticatedUser> authenticate(String token) {
        if (!enabled || token == null || token.isBlank()) {
            return Optional.empty();
        }
        byte[] suppliedHash = hash(token);
        AuthenticatedUser matchedUser = null;
        for (TokenIdentity identity : identities) {
            if (MessageDigest.isEqual(identity.tokenHash(), suppliedHash)) {
                matchedUser = new AuthenticatedUser(identity.subject(), identity.role());
            }
        }
        return Optional.ofNullable(matchedUser);
    }

    private void addIdentity(String token, String subject, ApiRole role) {
        if (token != null && token.length() >= 32 && !token.startsWith("replace_")
                && subject != null && !subject.isBlank()) {
            byte[] tokenHash = hash(token);
            if (identities.stream().anyMatch(existing -> MessageDigest.isEqual(existing.tokenHash(), tokenHash))) {
                throw new IllegalArgumentException("Local development access tokens must be unique.");
            }
            identities.add(new TokenIdentity(tokenHash, subject.trim(), role));
        }
    }

    private byte[] hash(String token) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private record TokenIdentity(byte[] tokenHash, String subject, ApiRole role) {
    }
}
