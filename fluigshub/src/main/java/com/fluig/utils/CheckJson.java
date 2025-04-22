package com.fluig.utils;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.util.List;

public class CheckJson {
    public static boolean checkInputPartsIsNullOrEmpty(List<InputPart> inputParts) {
        return inputParts == null || inputParts.isEmpty();
    }

}
