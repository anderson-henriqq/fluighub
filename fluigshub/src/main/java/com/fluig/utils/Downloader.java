package com.fluig.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class Downloader {

    public Downloader() {
    }

    public static byte[] downloadFileToMemory(String urlFile) throws IOException {


        URL website = new URL(urlFile);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        try {
            while (rbc.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    baos.write(buffer.get());
                }
                buffer.clear();
            }
        } finally {
            rbc.close();
        }
        return baos.toByteArray();
    }

}


