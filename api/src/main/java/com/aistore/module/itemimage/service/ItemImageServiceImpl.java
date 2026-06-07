package com.aistore.module.itemimage.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.itemimage.converter.ItemImageConverter;
import com.aistore.module.itemimage.dto.CreateItemImageRequest;
import com.aistore.module.itemimage.dto.ItemImageQueryParam;
import com.aistore.module.itemimage.dto.UpdateItemImageRequest;
import com.aistore.module.itemimage.entity.ItemImage;
import com.aistore.module.itemimage.mapper.ItemImageMapper;
import com.aistore.module.itemimage.vo.ItemImageListResponse;
import com.aistore.module.itemimage.vo.ItemImageSummaryVO;
import com.aistore.module.itemimage.vo.ItemImageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 物料图片服务实现 */
@Service
@RequiredArgsConstructor
public class ItemImageServiceImpl implements ItemImageService {

    private final ItemImageMapper imageMapper;
    private final ItemImageConverter imageConverter;

    @Override
    @Transactional
    public ItemImageVO createImage(CreateItemImageRequest request) {
        ItemImage entity = imageConverter.toEntity(request);
        imageMapper.insert(entity);
        return imageConverter.toVO(imageMapper.selectById(entity.getId()));
    }

    @Override
    public ItemImageVO getImageById(Long id) {
        ItemImage entity = imageMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.itemImageNotFound();
        }
        return imageConverter.toVO(entity);
    }

    @Override
    public ItemImageListResponse listImages(ItemImageQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<ItemImage> page = new Page<>(pageNum, pageSize);
        IPage<ItemImage> result = imageMapper.selectImagePage(page, param.getSpuId(), param.getSkuId(), param.getLevelId(), param.getStatus());
        List<ItemImageSummaryVO> items = result.getRecords().stream().map(imageConverter::toSummaryVO).toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return ItemImageListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public ItemImageVO updateImage(Long id, UpdateItemImageRequest request) {
        ItemImage entity = imageMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.itemImageNotFound();
        }
        imageConverter.updateEntity(entity, request);
        imageMapper.updateById(entity);
        return imageConverter.toVO(imageMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteImage(Long id) {
        if (imageMapper.selectById(id) == null) {
            throw ResourceNotFoundException.itemImageNotFound();
        }
        imageMapper.deleteById(id);
    }

    @Override
    @Transactional
    public ItemImageVO updateImageStatus(Long id, UpdateStatusRequest request) {
        ItemImage entity = imageMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.itemImageNotFound();
        }
        entity.setStatus(request.getStatus());
        imageMapper.updateById(entity);
        return imageConverter.toVO(imageMapper.selectById(id));
    }
}
