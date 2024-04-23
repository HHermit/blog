package com.aurora.mapper;

import com.aurora.model.dto.UserAdminDTO;
import com.aurora.entity.UserAuth;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserAuthMapper extends BaseMapper<UserAuth> {
    
    /**
    * @Description: 获得用户信息列表
    * @Param: [current, size, conditionVO]
    * @return: java.util.List<com.aurora.model.dto.UserAdminDTO>
    */
    List<UserAdminDTO> listUsers(@Param("current") Long current, @Param("size") Long size, @Param("conditionVO") ConditionVO conditionVO);

    /**
    * @Description: 获取用户数量
    * @Param: [conditionVO]
    * @return: java.lang.Integer
    */
    Integer countUser(@Param("conditionVO") ConditionVO conditionVO);

}
