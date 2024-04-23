package com.aurora.service;

import com.aurora.model.dto.FriendLinkAdminDTO;
import com.aurora.model.dto.FriendLinkDTO;
import com.aurora.entity.FriendLink;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.FriendLinkVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 获取好友链接列表
     */
    List<FriendLinkDTO> listFriendLinks();

    /**
     * 后台管理员视图获取好友链接列表
     */
    PageResultDTO<FriendLinkAdminDTO> listFriendLinksAdmin(ConditionVO conditionVO);

    /**
     * 保存或更新好友链接
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

}
