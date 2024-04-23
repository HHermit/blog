package com.aurora.service;

import com.aurora.model.dto.*;
import com.aurora.entity.Article;
import com.aurora.model.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface ArticleService extends IService<Article> {

    /**
     * 获取置顶和精选文章列表
     */
    TopAndFeaturedArticlesDTO listTopAndFeaturedArticles();

    /**
     * 列出所有文章
     */
    PageResultDTO<ArticleCardDTO> listArticles();

    /**
     * 根据类别ID列出文章
     * @param categoryId 类别的ID
     */
    PageResultDTO<ArticleCardDTO> listArticlesByCategoryId(Integer categoryId);

    /**
     * 根据文章ID获取文章详情
     * @param articleId 文章的ID
     * @return 返回对应文章ID的DTO
     */
    ArticleDTO getArticleById(Integer articleId);

    /**
     * 访问文章，校验文章的访问密码
     * @param articlePasswordVO 包含文章ID和密码的VO（值对象）
     */
    void accessArticle(ArticlePasswordVO articlePasswordVO);

    /**
     * 根据标签ID列出文章列表
     * @param tagId 标签的ID
     * @return 返回根据标签ID得到的文章卡牌DTO的分页结果
     */
    PageResultDTO<ArticleCardDTO> listArticlesByTagId(Integer tagId);

    /**
     * 列出 归档文章 集合 按时间降序（归档：将一个月份的文章放在一起）
     * @return 返回归档DTO的分页结果
     */
    PageResultDTO<ArchiveDTO> listArchives();

    /**
     * 管理员视图的文章列表
     * 并给每篇文章附上浏览量
     * @param conditionVO 查询条件的VO
     */
    PageResultDTO<ArticleAdminDTO> listArticlesAdmin(ConditionVO conditionVO);

    /**
     * 保存或更新文章 ，同时发送邮件通知订阅博客的用户，有新文章更新
     * @param articleVO 文章的VO
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 更新文章的指定和精选状态
     * @param articleTopFeaturedVO 包含文章ID和顶部/精选状态的VO
     */
    void updateArticleTopAndFeatured(ArticleTopFeaturedVO articleTopFeaturedVO);

    /**
     * 更新文章的删除状态
     * @param deleteVO 包含删除信息的VO
     */
    void updateArticleDelete(DeleteVO deleteVO);

    /**
     * 批量删除多篇文章
     * @param articleIds 文章ID的列表
     */
    void deleteArticles(List<Integer> articleIds);

    /**
     * 管理员视图中根据文章ID获取文章详情
     * @param articleId 文章的ID
     * @return 返回管理员视图下的文章详情DTO
     */
    ArticleAdminViewDTO getArticleByIdAdmin(Integer articleId);

    /**
     * 批量导出文章到文件服务器中（OSS或者MinIO），返回对应的访问的url链接
     * @param articleIdList 文章ID的列表
     * @return 返回文章内容的字符串列表
     */
    List<String> exportArticles(List<Integer> articleIdList);

    /**
     * 根据搜索关键词，利用对应的搜索策略（es 或者 mysql） 列出相关的文章
     * @param condition 搜索条件
     * @return 返回搜索结果的文章DTO列表
     */
    List<ArticleSearchDTO> listArticlesBySearch(ConditionVO condition);

}
