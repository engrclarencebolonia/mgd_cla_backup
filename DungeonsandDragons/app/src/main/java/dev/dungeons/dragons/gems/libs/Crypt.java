package dev.dungeons.dragons.gems.libs;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";

    public String encrypt(String message, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String message, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(METHOD);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        byte[] decryptedBytes = cipher.doFinal(Base64.decode(message, Base64.DEFAULT));

        // Trim the IV from the decrypted bytes
        byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
        System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);

        return new String(trimmedBytes, StandardCharsets.UTF_8);
    }
}
