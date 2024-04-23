package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传策略：OSS 或 MinIO
 */
@Getter
@AllArgsConstructor
public enum UploadModeEnum {

    OSS("oss", "ossUploadStrategyImpl"),

    MINIO("minio", "minioUploadStrategyImpl");

    private final String mode;

    private final String strategy;

    //获取spring容器中mode对应的资源上传服务实现类的beanid
    public static String getStrategy(String mode) {
        for (UploadModeEnum value : UploadModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}
