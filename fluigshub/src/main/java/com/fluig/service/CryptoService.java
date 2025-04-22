package com.fluig.service;

import com.fluig.model.CryptoModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.Crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoService {

    public ResponseGeneralModel executeService(CryptoModel request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String message = "";
        if (request.getEndpoint().equalsIgnoreCase("crypto")) {
            message = Crypto.crypt(request.getPassphrase());
            System.out.println("TEXTO CRIPTOGRAFADO COM SUCESSO");
        } else if (request.getEndpoint().equalsIgnoreCase("decrypto")) {
            message = Crypto.decrypt(request.getPassphrase());
            System.out.println("TEXTO DECRIPTOGRAFADO COM SUCESSO");
        }
        return new ResponseGeneralModel(message, false, 200);
    }
}
