package com.aistore.module.itemimage.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.itemimage.dto.CreateItemImageRequest;
import com.aistore.module.itemimage.dto.ItemImageQueryParam;
import com.aistore.module.itemimage.dto.UpdateItemImageRequest;
import com.aistore.module.itemimage.vo.ItemImageListResponse;
import com.aistore.module.itemimage.vo.ItemImageVO;

/** 物料图片服务接口 */
public interface ItemImageService {
    ItemImageVO createImage(CreateItemImageRequest request);
    ItemImageVO getImageById(Long id);
    ItemImageListResponse listImages(ItemImageQueryParam param);
    ItemImageVO updateImage(Long id, UpdateItemImageRequest request);
    void deleteImage(Long id);
    ItemImageVO updateImageStatus(Long id, UpdateStatusRequest request);
}
