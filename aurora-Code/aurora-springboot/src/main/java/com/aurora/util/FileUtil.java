package com.aurora.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.Objects;


/**
 * @author 33477
 * @description 文件工具类，封装一些常用方法
 */
@Log4j2
public class FileUtil {

    /**
     * 根据输入流计算MD5值
     * @param inputStream 要计算MD5值的输入流
     * @return 计算得到的MD5字符串，如果过程中发生异常则返回null
     */
    public static String getMd5(InputStream inputStream) {
        try {
            // 获取MD5算法进行消息摘要，最终生成一个md5值
            MessageDigest md5 = MessageDigest.getInstance("md5");
            // 缓冲区
            byte[] buffer = new byte[8192];
            int length;
            // 读取输入流数据，更新MD5摘要
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            // 将计算得到的摘要转换为十六进制字符串
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 尝试关闭输入流
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件名的扩展名
     * @param fileName 文件名
     * @return 扩展名，如果文件名为空或不含扩展名则返回空字符串
     */
    public static String getExtName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        // 从文件名中提取扩展名
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 创建一个临时文件，将multipartFile对象转换为文件File对象
     * @param multipartFile 多部分文件对象
     * @return 转换后的文件，如果转换过程中出现异常则返回null
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            // 从multipartFile中获取原始文件名
            String originalFilename = multipartFile.getOriginalFilename();
            //split() 中的内容是正则表达式
            // 正则表达式的点字符 . 通常匹配任何单个字符（除换行符外）。
            // 由于Java中字符串字面量中反斜杠 \ 是转义字符，因此若要在字符串中表示正则表达式中的原生.字符，需要对\进行转义，即使用两个反斜杠 \\。
            String[] filename = Objects.requireNonNull(originalFilename).split("\\.");
            // 创建临时文件，并将multipartFile内容写入
            // 在默认的临时文件目录中创建一个空文件，使用给定的前缀和后缀来生成其名称。然后将这个对象补充到之前创建的file中
            file = File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            // 确保程序退出时文件被删除
            // 清理掉创建的临时文件
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 根据文件大小计算准确性评分
     * @param size 文件大小（字节）
     * @return 计算得到的准确性评分
     */
    private static double getAccuracy(long size) {
        double accuracy;
        // 根据文件大小不同，设定不同的准确性评分
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2048) {
            accuracy = 0.6;
        } else if (size < 3072) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }

}
