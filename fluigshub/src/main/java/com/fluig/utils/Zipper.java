package com.fluig.utils;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    public Zipper() {
    }

    public void zipDirectory(byte[] fileBytes, String fileName, ZipOutputStream zos) throws IOException {
        {
            zos.putNextEntry(new ZipEntry(fileName));
            zos.write(fileBytes, 0, fileBytes.length);
            zos.closeEntry();

        }
    }
}