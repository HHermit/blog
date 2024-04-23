package com.aurora.strategy.impl;

import com.aurora.config.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service("minioUploadStrategyImpl")
public class MinioUploadStrategyImpl extends AbstractUploadStrategyImpl {

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 检查MinIO服务中指定的文件路径是否存在。
     *
     * @param filePath 要检查的文件路径
     * @return 返回文件是否存在的布尔值。存在返回true，不存在返回false。
     */
    @Override
    public Boolean exists(String filePath) {
        boolean exist = true;
        // 默认假设文件存在
        try {
            // 尝试获取MinIO客户端并统计指定路径的文件对象，如果文件存在，不会抛出异常
            getMinioClient()
                    .statObject(StatObjectArgs.builder()
                                              .bucket(minioProperties.getBucketName())
                                                // 指定存储桶名称
                                              .object(filePath)
                                                // 指定文件路径
                                              .build()
                                );
        } catch (Exception e) {
            exist = false;
            // 如果捕获到异常，表示文件不存在
        }
        return exist;
        // 返回文件存在的布尔值
    }


    /**
     * 上传文件到MinIO服务器。
     *
     * @param path 上传文件在存储桶中的路径。
     * @param fileName 上传文件的名称。
     * @param inputStream 文件的输入流。
     * @throws Exception 抛出异常处理任何MinIO操作错误。
     */
    @SneakyThrows
    @Override
    public void upload(String path, String fileName, InputStream inputStream) {
        // 使用MinIO客户端将输入流中的文件上传到指定的存储桶和路径
        getMinioClient()
                .putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        // 指定存储桶名称
                        .object(path + fileName)
                        // 指定上传后的文件路径和名称
                        .stream(inputStream, inputStream.available(), -1)
                        // 从输入流上传文件，-1表示上传整个输入流
                        .build());
    }

    /**
     *
     * @param filePath
     * @return 返回文件minio的访问地址
     */
    @Override
    public String getFileAccessUrl(String filePath) {
        return minioProperties.getUrl() + filePath;
    }

    /**
     * 获取Minio客户端的实例，方便进行上传操作
     * @return
     */
    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

}
