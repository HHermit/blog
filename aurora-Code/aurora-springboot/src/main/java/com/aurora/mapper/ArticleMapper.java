package com.aurora.mapper;

import com.aurora.model.dto.ArticleAdminDTO;
import com.aurora.model.dto.ArticleCardDTO;
import com.aurora.model.dto.ArticleDTO;
import com.aurora.model.dto.ArticleStatisticsDTO;
import com.aurora.entity.Article;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取顶部和特色文章列表,在blog首页展示
     * order by is_top desc（置顶在前）, is_featured desc（特色在前）
     * @return 返回文章卡片信息的列表
     */
    List<ArticleCardDTO> listTopAndFeaturedArticles();

    /**
     * 分页获取文章列表
     * @param current 当前页码
     * @param size 每页的文章数量
     * @return 返回根据页码和大小分页后的文章卡片信息列表
     */
    List<ArticleCardDTO> listArticles(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据分类ID分页获取文章列表
     * @param current 当前页码
     * @param size 每页的文章数量
     * @param categoryId 文章所属的分类ID
     * @return 返回根据分类ID、页码和大小分页后的文章卡片信息列表
     */
    List<ArticleCardDTO> getArticlesByCategoryId(@Param("current") Long current, @Param("size") Long size, @Param("categoryId") Integer categoryId);

    /**
     * 通过文章ID获取文章详情
     * @param articleId 文章的ID
     * @return 返回对应文章ID的详细信息
     */
    ArticleDTO getArticleById(@Param("articleId") Integer articleId);

    /**
     * 获取指定文章ID的前一篇文章卡片信息
     * @param articleId 当前文章的ID
     * @return 返回前一篇文章的卡片信息
     */
    ArticleCardDTO getPreArticleById(@Param("articleId") Integer articleId);

    /**
     * 获取指定文章ID的后一篇文章卡片信息
     * @param articleId 当前文章的ID
     * @return 返回后一篇文章的卡片信息
     */
    ArticleCardDTO getNextArticleById(@Param("articleId") Integer articleId);

    /**
     * 获取第一篇文章的卡片信息
     * @return 返回第一篇文章的卡片信息
     */
    ArticleCardDTO getFirstArticle();

    /**
     * 获取最后一篇文章的卡片信息
     * @return 返回最后一篇文章的卡片信息
     */
    ArticleCardDTO getLastArticle();

    /**
     * 根据标签ID分页获取文章列表
     * @param current 当前页码
     * @param size 每页的文章数量
     * @param tagId 文章所属的标签ID
     * @return 返回根据标签ID、页码和大小分页后的文章卡片信息列表
     */
    List<ArticleCardDTO> listArticlesByTagId(@Param("current") Long current, @Param("size") Long size, @Param("tagId") Integer tagId);

    /**
     * 获取归档文章的列表（分页）
     * @param current 当前页码
     * @param size 每页的文章数量
     * @return 返回归档文章的卡片信息列表
     */
    List<ArticleCardDTO> listArchives(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据条件统计管理员文章数量
     * @param conditionVO 统计条件的载体对象
     * @return 返回满足条件的文章管理员数量
     */
    Integer countArticleAdmins(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 分页 列出管理员视图的 文章
     * @param current 当前页码
     * @param size 每页的文章数量
     * @param conditionVO 查询条件对象
     * @return 返回管理员文章列表
     */
    List<ArticleAdminDTO> listArticlesAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 列出文章统计信息
     * @return 返回文章统计信息的列表
     */
    List<ArticleStatisticsDTO> listArticleStatistics();


}

