package com.aistore.module.supplier.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.supplier.converter.SupplierConverter;
import com.aistore.module.supplier.dto.CreateSupplierRequest;
import com.aistore.module.supplier.dto.SupplierQueryParam;
import com.aistore.module.supplier.dto.UpdateSupplierRequest;
import com.aistore.module.supplier.entity.Supplier;
import com.aistore.module.supplier.mapper.SupplierMapper;
import com.aistore.module.supplier.vo.SupplierListResponse;
import com.aistore.module.supplier.vo.SupplierSummaryVO;
import com.aistore.module.supplier.vo.SupplierVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 供应商服务实现 */
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;
    private final SupplierConverter supplierConverter;

    @Override
    @Transactional
    public SupplierVO createSupplier(CreateSupplierRequest request) {
        if (supplierMapper.selectCount(new LambdaQueryWrapper<Supplier>().eq(Supplier::getName, request.getName())) > 0) {
            throw DuplicateResourceException.duplicateSupplierName();
        }
        if (supplierMapper.selectCount(new LambdaQueryWrapper<Supplier>().eq(Supplier::getCode, request.getCode())) > 0) {
            throw DuplicateResourceException.duplicateSupplierCode();
        }
        Supplier entity = supplierConverter.toEntity(request);
        supplierMapper.insert(entity);
        return supplierConverter.toVO(supplierMapper.selectById(entity.getId()));
    }

    @Override
    public SupplierVO getSupplierById(Long id) {
        Supplier entity = supplierMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.supplierNotFound();
        }
        return supplierConverter.toVO(entity);
    }

    @Override
    public SupplierListResponse listSuppliers(SupplierQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        IPage<Supplier> result = supplierMapper.selectSupplierPage(page, param.getKeyword(), param.getStatus());
        List<SupplierSummaryVO> items = result.getRecords().stream().map(supplierConverter::toSummaryVO).toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return SupplierListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public SupplierVO updateSupplier(Long id, UpdateSupplierRequest request) {
        Supplier entity = supplierMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.supplierNotFound();
        }
        if (request.getName() != null && !request.getName().equals(entity.getName())
                && supplierMapper.selectCount(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getName, request.getName()).ne(Supplier::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateSupplierName();
        }
        supplierConverter.updateEntity(entity, request);
        supplierMapper.updateById(entity);
        return supplierConverter.toVO(supplierMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        if (supplierMapper.selectById(id) == null) {
            throw ResourceNotFoundException.supplierNotFound();
        }
        supplierMapper.deleteById(id);
    }

    @Override
    @Transactional
    public SupplierVO updateSupplierStatus(Long id, UpdateStatusRequest request) {
        Supplier entity = supplierMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.supplierNotFound();
        }
        entity.setStatus(request.getStatus());
        supplierMapper.updateById(entity);
        return supplierConverter.toVO(supplierMapper.selectById(id));
    }
}
