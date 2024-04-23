package com.aurora.service;

import com.aurora.model.dto.*;
import com.aurora.model.vo.*;

import java.util.List;

public interface UserAuthService {

    /**
     * 发送验证码给指定用户。
     */
    void sendCode(String username);

    /**
     * 查询 用户/游客 区域信息。
     */
    List<UserAreaDTO> listUserAreas(ConditionVO conditionVO);

    /**
     * 注册新用户。
     */
    void register(UserVO userVO);

    /**
     * 更新用户密码。
     */
    void updatePassword(UserVO userVO);

    /**
     * 后台更新管理员密码。
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 分页查询用户信息。
     */
    PageResultDTO<UserAdminDTO> listUsers(ConditionVO condition);

    /**
     * 用户登出操作。
     */
    UserLogoutStatusDTO logout();

    /**
     * 使用QQ账号登录。
     */
    UserInfoDTO qqLogin(QQLoginVO qqLoginVO);

}
