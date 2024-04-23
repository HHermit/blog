package com.aurora.mapper;

import com.aurora.model.dto.UniqueViewDTO;
import com.aurora.entity.UniqueView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UniqueViewMapper extends BaseMapper<UniqueView> {

    /**
    * @Description: 获取指定时间范围内的访问量  也就是 startTime <  。。。 <=  endTime 共5天时间内的访问量
     * 因为今天的在今天凌晨才会计入数据库
    * @Param: [startTime, endTime]
    * @return: java.util.List<com.aurora.model.dto.UniqueViewDTO>
    */
    List<UniqueViewDTO> listUniqueViews(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
