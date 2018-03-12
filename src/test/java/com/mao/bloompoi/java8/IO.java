package com.mao.bloompoi.java8;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author tsy
 * @Description
 * @date 20:09 2018/3/1
 */
public class IO {

    /**
     * 将InputStream转换为字符串。
     */
    public static String convertInputStreamToString(final InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * 将文件内容读入字符串。
     */
    public static String readFileAsString(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    /**
     * 获取当前工作目录。
     */
    public static String getCurrentWorkingDirectoryPath() {
        return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    }
}
