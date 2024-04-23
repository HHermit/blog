package com.aurora.service;

import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.UserInfoDTO;
import com.aurora.model.dto.UserOnlineDTO;
import com.aurora.entity.UserInfo;
import com.aurora.model.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 更新用户信息
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 更新用户头像
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 保存用户邮箱信息 校验验证码
     */
    void saveUserEmail(EmailVO emailVO);

    /**
     * 更新用户订阅信息
     */
    void updateUserSubscribe(SubscribeVO subscribeVO);

    /**
     * 更新用户角色
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 更新用户禁用状态
     */
    void updateUserDisable(UserDisableVO userDisableVO);

    /**
     * 列出在线用户
     */
    PageResultDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    /**
     * 移除在线用户
     */
    void removeOnlineUser(Integer userInfoId);

    /**
     * 根据ID获取用户信息
     */
    UserInfoDTO getUserInfoById(Integer id);


}
