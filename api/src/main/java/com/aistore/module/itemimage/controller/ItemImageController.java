package com.aistore.module.itemimage.controller;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.module.itemimage.dto.CreateItemImageRequest;
import com.aistore.module.itemimage.dto.ItemImageQueryParam;
import com.aistore.module.itemimage.dto.UpdateItemImageRequest;
import com.aistore.module.itemimage.service.ItemImageService;
import com.aistore.module.itemimage.vo.ItemImageListResponse;
import com.aistore.module.itemimage.vo.ItemImageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 物料图片管理 Controller */
@RestController
@RequestMapping("/api/item-images")
@RequiredArgsConstructor
public class ItemImageController {

    private final ItemImageService imageService;

    @PostMapping
    public ResponseEntity<ItemImageVO> create(@Valid @RequestBody CreateItemImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.createImage(request));
    }

    @GetMapping
    public ResponseEntity<ItemImageListResponse> list(
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        ItemImageQueryParam param = ItemImageQueryParam.builder()
                .spuId(spuId).skuId(skuId).levelId(levelId).status(status)
                .page(page != null ? page : 1).pageSize(pageSize != null ? pageSize : 20).build();
        return ResponseEntity.ok(imageService.listImages(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemImageVO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemImageVO> update(@PathVariable Long id, @Valid @RequestBody UpdateItemImageRequest request) {
        return ResponseEntity.ok(imageService.updateImage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ItemImageVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(imageService.updateImageStatus(id, request));
    }
}
