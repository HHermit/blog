package com.aurora.mapper;

import com.aurora.model.dto.TagAdminDTO;
import com.aurora.model.dto.TagDTO;
import com.aurora.entity.Tag;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 获取所有标签信息的列表。
     * @return 返回TagDTO类型的标签信息列表。
     */
    List<TagDTO> listTags();

    /**
     * 获取排名前10的标签信息列表。
     * @return 返回TagDTO类型的前10个标签信息列表。
     */
    List<TagDTO> listTopTenTags();

    /**
     * 根据文章ID获取该文章关联的标签名称列表。
     * @param articleId 文章的ID。
     * @return 返回String类型的标签名称列表。
     */
    List<String> listTagNamesByArticleId(Integer articleId);

    /**
     * 分页查询标签信息（后台管理员视图）。
     * @return 返回TagAdminDTO类型的标签信息分页列表。
     */
    List<TagAdminDTO> listTagsAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);
}
