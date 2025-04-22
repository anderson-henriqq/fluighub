package com.fluig.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Crypto {
    private static final String chaveSecretaBase64 = "CojwTwXwuwXpyHoeqLigAA==";
    private static final byte[] chaveSecretaBytes = Base64.getDecoder().decode(chaveSecretaBase64);
    private static final SecretKey chaveSecreta = new SecretKeySpec(chaveSecretaBytes, "AES");

    public static String crypt(String valor) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, chaveSecreta);
        byte[] valorCriptografado = cipher.doFinal(valor.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(valorCriptografado);
    }


    public static String decrypt(String valorCriptografado) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, chaveSecreta);
        byte[] valorDescriptografado = cipher.doFinal(Base64.getDecoder().decode(valorCriptografado));
        return new String(valorDescriptografado, StandardCharsets.UTF_8);
    }
}
