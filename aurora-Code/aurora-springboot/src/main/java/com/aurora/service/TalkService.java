package com.aurora.service;

import com.aurora.model.dto.TalkAdminDTO;
import com.aurora.model.dto.TalkDTO;
import com.aurora.entity.Talk;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.TalkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface TalkService extends IService<Talk> {

    /**
     * 前台：获取说说列表。
    */
    PageResultDTO<TalkDTO> listTalks();

    /**
     * 根据说说ID获取特定说说。
     */
    TalkDTO getTalkById(Integer talkId);

    /**
     * 保存或更新说说信息。
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 批量删除说说。
     */
    void deleteTalks(List<Integer> talkIdList);

    /**
     * 获取后台管理界面的说说列表。
     */
    PageResultDTO<TalkAdminDTO> listBackTalks(ConditionVO conditionVO);

    /**
     * 根据说说ID获取后台管理界面的特定说说。
     */
    TalkAdminDTO getBackTalkById(Integer talkId);

}
