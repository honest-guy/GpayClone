package codinghacks.org.gpayclone.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionHelper {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // Use AES-256 for strong encryption
        return keyGenerator.generateKey();
    }

    // Encrypt a string
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);
        String result = Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT);
        return result;
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        // Decode the Base64 encoded data
        byte[] decodedData = Base64.decode(encryptedData, Base64.DEFAULT);


        if (decodedData.length < 12) {
            throw new IllegalArgumentException("Invalid encrypted data: not enough bytes for IV");
        }


        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
        byte[] iv = new byte[12];
        byteBuffer.get(iv);


        byte[] encryptedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedBytes);


        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);


        byte[] decryptedData = cipher.doFinal(encryptedBytes);


        return new String(decryptedData, StandardCharsets.UTF_8);
    }




}



