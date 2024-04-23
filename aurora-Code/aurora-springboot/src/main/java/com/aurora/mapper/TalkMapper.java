package com.aurora.mapper;

import com.aurora.model.dto.TalkAdminDTO;
import com.aurora.model.dto.TalkDTO;
import com.aurora.entity.Talk;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 获取当前页和每页数量的 说说列表
     */
    List<TalkDTO> listTalks(@Param("current") Long current, @Param("size") Long size);

    /**
     * 根据说说ID获取说说信息
     */
    TalkDTO getTalkById(@Param("talkId") Integer talkId);

    /**
     * 获取管理员视图下的当前页和每页数量的说说列表，可根据条件筛选
     */
    List<TalkAdminDTO> listTalksAdmin(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 根据说说ID获取管理员视图下的说说信息
     */
    TalkAdminDTO getTalkByIdAdmin(@Param("talkId") Integer talkId);
}
