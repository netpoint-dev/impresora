package com.common.demo.power;

import android.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    private static String FILE_PATH = "";

    // 设置文件路径
    public static void setFilePath(String filePath) {
        Log.d("DataStorage", "File path set to: " + filePath);
        FILE_PATH = filePath;
        Path path = Paths.get(FILE_PATH);
        // 检查文件是否存在，如果不存在则创建
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
                Log.d("DataStorage", "File created at: " + FILE_PATH);
            }
        } catch (IOException e) {
            Log.e("DataStorage", "Failed to create file at: " + FILE_PATH, e);
        }
    }

    // 保存数据，覆盖相同key的值，添加新的key时换行追加
    public static void saveData(String key, String value) {
        try {
            Path path = Paths.get(FILE_PATH);

            // 检查文件是否存在，如果不存在则创建
            try {
                if (!Files.exists(path)) {
                    Files.createFile(path);
                    Log.d("DataStorage", "File created at: " + FILE_PATH);
                }
            } catch (IOException e) {
                Log.e("DataStorage", "Failed to create file at: " + FILE_PATH, e);
            }

            // 读取文件中的所有行
            List<String> lines = Files.readAllLines(path);
            boolean keyExists = false;

            // 遍历所有行，检查是否已存在该key
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith(key + ": ")) {
                    // 如果找到相同的key，替换其值
                    lines.set(i, key + ": " + value);
                    keyExists = true;
                    break;
                }
            }

            // 如果key不存在，则在文件末尾添加新的key: value
            if (!keyExists) {
                lines.add(key + ": " + value);
            }

            // 将更新后的内容写回文件
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Log.d("DataStorage", "Data saved: " + key + " : " + value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载数据，根据key查找对应的值
    public static String loadData(String key) {
        try {
            Path path = Paths.get(FILE_PATH);

            // 检查文件是否存在，如果不存在则创建
            try {
                if (!Files.exists(path)) {
                    Files.createFile(path);
                    Log.d("DataStorage", "File created at: " + FILE_PATH);
                }
            } catch (IOException e) {
                Log.e("DataStorage", "Failed to create file at: " + FILE_PATH, e);
            }

            // 读取文件中的所有行
            List<String> lines = Files.readAllLines(path);
            // 查找匹配的key
            for (String line : lines) {
                if (line.startsWith(key + ": ")) {
                    // 返回与key对应的value，去掉前缀 "key: "
                    return line.split(": ")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;  // 未找到对应的key时返回null
    }
}
