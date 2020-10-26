package com.coodev.androidcollection.Utils.io;

import java.io.File;
import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.GzipSink;
import okio.GzipSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class OkIOUtil {
    public static void read(File file) {
        BufferedSource bufferedSource = null;
        try {
            Source fileSource = Okio.source(file);
            bufferedSource = Okio.buffer(fileSource);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedSource != null) {
                try {
                    bufferedSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void write(File file) {
        BufferedSink bufferedSink = null;
        try {
            Sink fileSource = Okio.sink(file);
            bufferedSink = Okio.buffer(fileSource);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedSink != null) {
                try {
                    bufferedSink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void gzipWrite(File file) {
        try (GzipSink gzipSink = new GzipSink(Okio.sink(file));
             BufferedSink buffer = Okio.buffer(gzipSink);) {// try - catch - resource 会自动关闭资源
            buffer.writeUtf8("test string");
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void gzipRead(File file) {
        try (GzipSource gzipSink = new GzipSource(Okio.source(file));
             BufferedSource buffer = Okio.buffer(gzipSink);) {// try - catch - resource 会自动关闭资源
            final String s = buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ByteString readByteString(File file) throws IOException {
        try (BufferedSource source = Okio.buffer(Okio.source(file))) {
            return source.readByteString();
        }
    }

    public Buffer readBuffer(File file) throws IOException {
        try (Source source = Okio.source(file)) {
            Buffer buffer = new Buffer();
            buffer.writeAll(source);
            return buffer;
        }
    }
}
