package com.framework.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtil
 *
 * @author D.Yeung
 * @since 2019.04.24
 */
public class FileUtil {

    /**
     * 创建文件
     *
     * @param file 文件
     * @return 创建成功为true
     */
    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 创建成功为true
     */
    public static boolean createFile(String filePath) {
        return createFile(new File(filePath));
    }

    /**
     * 创建文件夹
     *
     * @param directory 文件夹
     * @return 创建成功为true
     */
    public static boolean createDirectory(File directory) {
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }

    /**
     * 创建文件夹
     *
     * @param directoryPath 文件夹路径
     * @return 创建成功为true
     */
    public static boolean createDirectory(String directoryPath) {
        return createDirectory(new File(directoryPath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 删除成功为true
     */
    public static boolean deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 删除成功为true
     */
    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件夹
     *
     * @param directory 文件夹
     * @return 删除成功为true
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            return directory.delete();
        }
        return false;
    }

    /**
     * 删除文件夹
     *
     * @param directoryPath 文件夹路径
     * @return 删除成功为true
     */
    public static boolean deleteDirectory(String directoryPath) {
        return deleteDirectory(new File(directoryPath));
    }

    /**
     * 删除文件夹及文件夹下所有文件
     *
     * @param paths 路径
     * @return 删除成功为true
     */
    public static boolean deleteAll(List<String> paths) {
        for (String path: paths) {
            if (!deleteAll(path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 遍历文件夹中所有文件
     *
     * @param directory 文件夹
     * @return 文件
     */
    public static List<File> listFiles(File directory) {
        return traversingDirectory(directory, new ArrayList<>());
    }

    /**
     * 处理文件名中文乱码
     *
     * @param fileName 文件名
     * @return 文件名
     */
    public static String transformCH(String fileName) {
        return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    /**
     * 删除全部
     *
     * @param path 路径
     * @return 删除成功为true
     */
    private static boolean deleteAll(String path) {

        // 补齐路径
        path += path.endsWith(File.separator) ? "" : File.separator;

        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return false;
        }

        // 文件
        if (dirFile.isFile()) {
            return deleteFile(path);
        }

        // 文件夹
        File[] files = dirFile.listFiles();
        if (null != files) {
            for (File file: files) {
                if (file.isFile()) {
                    if (!deleteFile(file.getAbsolutePath())) {
                        return false;
                    }
                } else {
                    if (!deleteAll(file.getAbsolutePath())) {
                        return false;
                    }
                }
            }
        }

        // 删除当前文件夹
        return dirFile.delete();
    }

    /**
     * 遍历文件夹中所有文件
     *
     * @param directory 文件夹
     * @param results 返回结果（递归参数）
     * @return 文件
     */
    private static List<File> traversingDirectory(File directory, List<File> results) {

        if (!directory.exists()) {
            return results;
        }

        if (directory.isFile()) {
            results.add(directory);
            return results;
        }

        // 递归所有文件
        File[] files = directory.listFiles();
        if (null != files && files.length > 0) {
            for (File file: files) {
                if (file.isFile()) {
                    results.add(file);
                } else {
                    traversingDirectory(file, results);
                }
            }
        }

        return results;
    }
}