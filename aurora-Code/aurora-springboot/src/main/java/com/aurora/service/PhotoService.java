package com.aurora.service;

import com.aurora.entity.Photo;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.PhotoAdminDTO;
import com.aurora.model.dto.PhotoDTO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.DeleteVO;
import com.aurora.model.vo.PhotoInfoVO;
import com.aurora.model.vo.PhotoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PhotoService extends IService<Photo> {

    /**
     * 分页查询照片列表
     */
    PageResultDTO<PhotoAdminDTO> listPhotos(ConditionVO conditionVO);

    /**
     * 更新照片信息
     */
    void updatePhoto(PhotoInfoVO photoInfoVO);

    /**
     * 保存照片
     */
    void savePhotos(PhotoVO photoVO);

    /**
     * 更新照片所属相册信息
     */
    void updatePhotosAlbum(PhotoVO photoVO);

    /**
     * 删除照片，或者冲回收站里恢复
     */
    void updatePhotoDelete(DeleteVO deleteVO);

    /**
     * 批量删除照片（从回收站里删除）
     */
    void deletePhotos(List<Integer> photoIds);

    /**
     * 前台：根据相册ID查询照片列表
     */
    PhotoDTO listPhotosByAlbumId(Integer albumId);

}
