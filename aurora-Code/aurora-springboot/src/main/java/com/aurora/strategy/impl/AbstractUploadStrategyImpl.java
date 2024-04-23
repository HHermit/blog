package com.aurora.strategy.impl;

import com.aurora.exception.BizException;
import com.aurora.strategy.UploadStrategy;
import com.aurora.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {


    /**
     * 上传文件到指定路径
     *
     * @param file 要上传的文件，使用MultipartFile类型，封装了文件的各种信息
     * @param path 文件上传后的存储路径
     * @return 返回文件的访问URL
     * @throws BizException 如果文件上传过程中发生异常，则抛出业务异常
     */
    @Override
    public String uploadFile(MultipartFile file, String path) {
        try {
            // 计算文件的MD5值，用于文件去重
            String md5 = FileUtil.getMd5(file.getInputStream());
            // 获取文件的扩展名
            String extName = FileUtil.getExtName(file.getOriginalFilename());
            // 拼接文件名，采用MD5值加扩展名的方式，保证文件名的唯一性
            String fileName = md5 + extName;

            // 检查云端文件是否已存在（minio或者OSS），若不存在则执行上传
            if (!exists(path + fileName)) {
                // 执行文件上传逻辑
                upload(path, fileName, file.getInputStream());
            }

            // 返回上传后文件的访问URL
            return getFileAccessUrl(path + fileName);
        } catch (Exception e) {
            // 打印异常栈信息
            e.printStackTrace();
            // 抛出文件上传失败的业务异常
            throw new BizException("文件上传失败");
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String path) {
        try {
            upload(path, fileName, inputStream);
            return getFileAccessUrl(path + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }

    public abstract Boolean exists(String filePath);

    public abstract void upload(String path, String fileName, InputStream inputStream) throws IOException;

    public abstract String getFileAccessUrl(String filePath);

}
