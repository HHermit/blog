package com.aurora.strategy.context;

import com.aurora.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

import static com.aurora.enums.UploadModeEnum.getStrategy;

@Service
public class UploadStrategyContext {

    @Value("${upload.mode}")
    /*
      根据yml中配置方便后序选择OSS或者minio对应的功能调用
     */
    private String uploadMode;

    @Autowired
    /*
        利用springboot 的bean自动注入 将已经在spring中声明的所有UploadStrategy保存到 Map映射中
        主要就是后序 MinioUploadStrategyImpl还是MinioUploadStrategyImpl的使用
     */
    private Map<String, UploadStrategy> uploadStrategyMap;

    public String executeUploadStrategy(MultipartFile file, String path) {
        return uploadStrategyMap.get(getStrategy(uploadMode)).uploadFile(file, path);
    }

    /**
    * @Description: 执行上传资源到指定mode（OSS 或者 Minio）中
    * @Param: [fileName[文件名], inputStream[资源内容], path[在mode中的路径，用url表示]]
    * @return: java.lang.String
    */
    public String executeUploadStrategy(String fileName, InputStream inputStream, String path) {
        return uploadStrategyMap.get(getStrategy(uploadMode))//根据配置中的模式获取对应的服务实现类
                                .uploadFile(fileName, inputStream, path);//调对应的上传方法
    }

}
