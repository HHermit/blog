package com.aurora.strategy.impl;

import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.strategy.SearchStrategy;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.*;
import static com.aurora.enums.ArticleStatusEnum.PUBLIC;

@Log4j2
@Service("esSearchStrategyImpl")
public class EsSearchStrategyImpl implements SearchStrategy {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //查询接口
    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        if (StringUtils.isBlank(keywords)) {
            return new ArrayList<>();
        }
        return search(buildQuery(keywords));
    }

    /**
    * @Description: 根据 关键词 构建es搜索的查询器
    * @Param: [keywords]
    * @return: org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
    */
    private NativeSearchQueryBuilder buildQuery(String keywords) {
        // 创建一个用于构造Elasticsearch原生搜索查询的构建器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 创建一个布尔查询构建器，用来组合多个查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加必须满足的查询条件，关键词需至少匹配文章标题或文章内容中的一个
        boolQueryBuilder.must(QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("articleTitle", keywords))  // 文章标题匹配关键词
                .should(QueryBuilders.matchQuery("articleContent", keywords))); // 文章内容匹配关键词

        // 添加其他必须满足的条件：文章未被删除（isDelete字段值为FALSE）
        boolQueryBuilder.must(QueryBuilders.termQuery("isDelete", FALSE));

        // 添加最后一个必须满足的条件：文章状态为公开
        boolQueryBuilder.must(QueryBuilders.termQuery("status", PUBLIC.getStatus()));

        // 将上述构建的布尔查询设置到原生搜索查询构建器中
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        // 返回已配置好的原生搜索查询构建器
        return nativeSearchQueryBuilder;
    }


    /**
    * @Description: 使用Elasticsearch执行高级搜索操作，并将搜索结果中的标题和内容高亮显示后封装到ArticleSearchDTO对象列表中返回
    * @Param: [nativeSearchQueryBuilder]
    * @return: java.util.List<com.aurora.model.dto.ArticleSearchDTO>
    */
    private List<ArticleSearchDTO> search(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        //es内置高亮显示
        // 创建并配置标题高亮字段设置
        HighlightBuilder.Field titleField = new HighlightBuilder.Field("articleTitle");
        titleField.preTags(PRE_TAG); // 设置高亮标签前缀
        titleField.postTags(POST_TAG); // 设置高亮标签后缀

        // 创建并配置内容高亮字段设置
        HighlightBuilder.Field contentField = new HighlightBuilder.Field("articleContent");
        contentField.preTags(PRE_TAG); // 设置内容高亮标签前缀
        contentField.postTags(POST_TAG); // 设置内容高亮标签后缀
        contentField.fragmentSize(50); // 设置高亮片段的最大长度

        // 将上述高亮字段添加到搜索查询构建器中
        nativeSearchQueryBuilder.withHighlightFields(titleField, contentField);

        try {
            // 使用构建好的查询条件执行搜索
            SearchHits<ArticleSearchDTO> searchResults = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), ArticleSearchDTO.class);

            // 遍历搜索结果并对每个命中项进行处理：获取高亮标题和内容，并设置到ArticleSearchDTO对象中
            return searchResults.getSearchHits().stream()
                    .map(hit -> {
                        ArticleSearchDTO article = hit.getContent();
                        // 若存在标题高亮片段，则设置第一个片段作为文章标题
                        List<String> titleHighlights = hit.getHighlightFields().get("articleTitle");
                        if (CollectionUtils.isNotEmpty(titleHighlights)) {
                            article.setArticleTitle(titleHighlights.get(0));
                        }

                        // 若存在内容高亮片段，则设置最后一个片段作为文章内容
                        List<String> contentHighlights = hit.getHighlightFields().get("articleContent");
                        if (CollectionUtils.isNotEmpty(contentHighlights)) {
                            article.setArticleContent(contentHighlights.get(contentHighlights.size() - 1));
                        }

                        return article;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 当搜索过程中发生异常时记录错误信息
            log.error(e.getMessage());
        }

        // 在搜索失败或无结果的情况下返回一个空列表
        return new ArrayList<>();
    }


}

