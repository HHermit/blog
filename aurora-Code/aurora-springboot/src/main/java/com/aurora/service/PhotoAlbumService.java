package com.aurora.service;

import com.aurora.model.dto.PhotoAlbumAdminDTO;
import com.aurora.model.dto.PhotoAlbumDTO;
import com.aurora.entity.PhotoAlbum;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.vo.PhotoAlbumVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PhotoAlbumService extends IService<PhotoAlbum> {

    /**
     * 新疆或更新相册信息。
     */
    void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO);

    /**
     * 管理员视图下分页查询相册列表。
     */
    PageResultDTO<PhotoAlbumAdminDTO> listPhotoAlbumsAdmin(ConditionVO condition);

    /**
     * 管理员模式下查询所有相册信息。
     */
    List<PhotoAlbumDTO> listPhotoAlbumInfosAdmin();

    /**
     * 根据相册ID，管理员视图下  获取相册详细信息。
     */
    PhotoAlbumAdminDTO getPhotoAlbumByIdAdmin(Integer albumId);

    /**
     * 根据相册ID删除相册。（相册存在照片软删除，否则删除）
     */
    void deletePhotoAlbumById(Integer albumId);

    /**
     * 查询所有相册信息（非管理员模式）。
     */
    List<PhotoAlbumDTO> listPhotoAlbums();

}
