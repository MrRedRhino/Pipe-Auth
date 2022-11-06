package org.pipeman.pa.login;

import org.pipeman.penc.Penc;

import java.util.List;

public record Token(String username, long createdAt, long expiresAt) {
    private static Encryptor encryptor;

    public static void setEncryptorPassword(String password) {
        encryptor = new Encryptor(password);
    }

    public String encode() {
        return encryptor.encrypt(Penc.encode(username, String.valueOf(createdAt), String.valueOf(expiresAt)));
    }

    public static Token fromString(String token) {
        if (token == null) return null;

        try {
            List<String> decoded = Penc.decode(encryptor.decrypt(token));

            String name = decoded.get(0);
            long createdAt = Long.parseLong(decoded.get(1));
            long expiresAt = Long.parseLong(decoded.get(2));

            return new Token(name, createdAt, expiresAt);

        } catch (Exception ignored) {
            return null;
        }
    }
}
