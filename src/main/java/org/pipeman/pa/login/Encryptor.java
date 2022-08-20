package org.pipeman.pa.login;

import org.jasypt.util.text.StrongTextEncryptor;

public class Encryptor {
    private final StrongTextEncryptor encryptor;

    public Encryptor(String password) {
        this.encryptor = new StrongTextEncryptor();
        encryptor.setPassword(password);
    }

    public String encrypt(String text) {
        return encryptor.encrypt(text);
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}
