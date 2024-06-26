package com.aurora.mapper;

import com.aurora.model.dto.PhotoAlbumAdminDTO;
import com.aurora.entity.PhotoAlbum;
import com.aurora.model.vo.ConditionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {

    /**
    * @Description: 查询相册列表，返回相关信息，包含相册包含的照片数量
    * @Param: [current, size, conditionVO]
    * @return: java.util.List<com.aurora.model.dto.PhotoAlbumAdminDTO>
    */
    List<PhotoAlbumAdminDTO> listPhotoAlbumsAdmin(@Param("current") Long current, @Param("size") Long size, @Param("condition") ConditionVO conditionVO);

}
