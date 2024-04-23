package com.aurora.strategy.context;

import com.aurora.enums.MarkdownTypeEnum;
import com.aurora.strategy.ArticleImportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 *  @description:上传文件
 *  
*/
@Service
public class ArticleImportStrategyContext {

    //从spring容器中选择已经注册的ArticleImportStrategy的实现bean自动注入 ，方便后序直接使用对应的功能
    @Autowired
    private Map<String, ArticleImportStrategy> articleImportStrategyMap;

    /**
    * @Description: 最终调用上传服务实现类NormalArticleImportStrategyImpl实现导入细节
    * @Param: [file, type]
    * @return: void
    */
    public void importArticles(MultipartFile file, String type) {
        articleImportStrategyMap.get(MarkdownTypeEnum.getMarkdownType(type)).importArticles(file);
    }
}