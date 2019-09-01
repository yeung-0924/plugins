package com.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtil
 *
 * @author D.Yeung
 * @since 2019.04.28
 */
public class ZipUtil {

    /**
     * 单文件压缩
     *
     * @param srcPath 源文件路径
     * @param zipPath 压缩文件路径
     * @return 压缩文件
     */
    public static File compressFile(String srcPath, String zipPath) {
        return compressFile(new String[]{srcPath}, zipPath);
    }

    /**
     * 单文件压缩
     *
     * @param srcFile 源文件
     * @param zipFile 压缩文件
     * @return 压缩文件
     */
    public static File compressFile(File srcFile, File zipFile) {
        return compressFile(new File[]{srcFile}, zipFile);
    }

    /**
     * 多文件压缩
     *
     * @param srcPaths 源文件路径
     * @param zipPath 压缩文件路径
     * @return 压缩文件
     */
    public static File compressFile(String[] srcPaths, String zipPath) {
        File[] files = new File[srcPaths.length];
        for (int i = 0; i < srcPaths.length; i++) {
            files[i] = new File(srcPaths[i]);
        }
        return compressFile(files, new File(zipPath));
    }

    /**
     * 多文件压缩
     *
     * @param srcFiles 源文件
     * @param zipFile 压缩文件
     * @return 压缩文件
     */
    public static File compressFile(File[] srcFiles, File zipFile) {
        // 创建压缩文件
        FileUtil.createFile(zipFile);
        // 创建压缩流
        ZipOutputStream zos;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            // 写入压缩文件
            for (File srcFile: srcFiles) {
                if (srcFile.isFile()) {
                    // 将目标文件过滤
                    if (!zipFile.getAbsolutePath().equals(srcFile.getAbsolutePath())) {
                        // 文件压缩
                        doZip(srcFile, zos, "");
                    }
                } else {
                    // 文件夹压缩
                    compressFile(srcFile, zos, srcFile.getName(), zipFile);
                }
            }
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    /**
     * 文件夹压缩
     *
     * @param srcFile 文件夹
     * @param zos 压缩流
     * @param basePath 压缩目录
     * @param zipFile 压缩文件
     */
    private static void compressFile(File srcFile, ZipOutputStream zos, String basePath, File zipFile) {
        File[] files = srcFile.listFiles();
        if (null != files && files.length > 0) {
            for (File file: files) {
                if (file.isFile()) {
                    // 将目标文件过滤
                    if (!zipFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                        // 文件压缩
                        doZip(file, zos, basePath);
                    }
                } else {
                    // 递归
                    compressFile(file, zos, StringUtil.isNull(basePath) ? file.getName() : basePath + "/" + file.getName(), zipFile);
                }
            }
        }
    }

    /**
     * ZIP压缩
     *
     * @param file 源文件
     * @param zos 压缩流
     * @param basePath basePath 压缩目录
     */
    private static void doZip(File file, ZipOutputStream zos, String basePath) {
        try {
            zos.putNextEntry(new ZipEntry(StringUtil.isNull(basePath) ? file.getName() : basePath + "/" + file.getName()));
            int len;
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
                zos.flush();
            }
            zos.closeEntry();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}