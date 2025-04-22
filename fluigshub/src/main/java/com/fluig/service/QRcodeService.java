package com.fluig.service;

import com.fluig.model.QRcodeModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.QRCodeGenerator;

public class QRcodeService {
    private String QRcodeInB64;

    public ResponseGeneralModel executeService(QRcodeModel request) {
        this.QRcodeInB64 = request.getTexttobase64();
        String message = QRCodeGenerator.stringToBase64(QRcodeInB64);
        System.out.println("IMAGEM QRCODE FOI GERADA!");
        return new ResponseGeneralModel(message, false, 200);
    }
}
