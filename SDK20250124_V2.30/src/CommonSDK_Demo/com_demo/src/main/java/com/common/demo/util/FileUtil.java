package com.common.demo.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    /**
     * 保存扫码结果
     * @param data
     * @param fileName
     */
    public static void saveFile(String fileName, String dirName, String data) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File file = new File(dirName, fileName);
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write((data + "\n").getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName, String dirName) {
        File file = new File(dirName, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
