package com.aistore.module.customer.service;

import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.customer.converter.CustomerConverter;
import com.aistore.module.customer.dto.CreateCustomerRequest;
import com.aistore.module.customer.dto.CustomerQueryParam;
import com.aistore.module.customer.dto.UpdateCustomerRequest;
import com.aistore.module.customer.dto.UpdateCustomerStatusRequest;
import com.aistore.module.customer.entity.Customer;
import com.aistore.module.customer.entity.CustomerShipAddress;
import com.aistore.module.customer.mapper.CustomerMapper;
import com.aistore.module.customer.mapper.CustomerShipAddressMapper;
import com.aistore.module.customer.vo.CustomerListResponse;
import com.aistore.module.customer.vo.CustomerSummaryVO;
import com.aistore.module.customer.vo.CustomerVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 顾客服务实现类
 * 收/发货地址为一对多，存于 customer_ship_address 子表；更新时整列表替换。
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerMapper customerMapper;
    private final CustomerShipAddressMapper shipAddressMapper;
    private final CustomerConverter customerConverter;

    @Override
    @Transactional
    public CustomerVO createCustomer(CreateCustomerRequest request) {
        log.info("创建顾客: name={}, code={}, 地址数={}",
                request.getName(), request.getCode(),
                request.getShipAddresses() != null ? request.getShipAddresses().size() : 0);

        // 1. 唯一性校验 - 名称
        LambdaQueryWrapper<Customer> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(Customer::getName, request.getName());
        if (customerMapper.selectCount(nameQuery) > 0) {
            throw DuplicateResourceException.duplicateCustomerName();
        }

        // 2. 唯一性校验 - 编码
        LambdaQueryWrapper<Customer> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(Customer::getCode, request.getCode());
        if (customerMapper.selectCount(codeQuery) > 0) {
            throw DuplicateResourceException.duplicateCustomerCode();
        }

        // 3. 插入顾客主表
        Customer entity = customerConverter.toEntity(request);
        customerMapper.insert(entity);

        // 4. 插入送货地址子表
        customerConverter.toShipAddressEntities(entity.getId(), request.getShipAddresses())
                .forEach(shipAddressMapper::insert);
        log.info("顾客创建成功: id={}, name={}", entity.getId(), entity.getName());

        Customer saved = customerMapper.selectById(entity.getId());
        return customerConverter.toCustomerVO(saved, loadAddresses(entity.getId()));
    }

    @Override
    public CustomerVO getCustomerById(Long id) {
        log.debug("查询顾客详情: id={}", id);
        Customer entity = customerMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.customerNotFound();
        }
        return customerConverter.toCustomerVO(entity, loadAddresses(id));
    }

    @Override
    public CustomerListResponse listCustomers(CustomerQueryParam param) {
        log.debug("查询顾客列表: page={}, pageSize={}, keyword={}", param.getPage(), param.getPageSize(), param.getKeyword());

        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? param.getPageSize() : 20;
        if (pageSize > 100) {
            pageSize = 100;
        }

        Page<Customer> page = new Page<>(pageNum, pageSize);
        IPage<Customer> result = customerMapper.selectCustomerPage(page, param.getKeyword(), param.getStatus());

        List<Customer> records = result.getRecords();
        // 批量加载本页顾客的送货地址（一次查询，避免 N+1）
        Map<Long, List<CustomerShipAddress>> addrMap =
                loadAddressesForCustomers(records.stream().map(Customer::getId).toList());

        List<CustomerSummaryVO> items = records.stream()
                .map(c -> customerConverter.toCustomerSummaryVO(c, addrMap.getOrDefault(c.getId(), List.of())))
                .toList();

        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);

        return CustomerListResponse.builder()
                .items(items)
                .total(result.getTotal())
                .page(pageNum)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    @Override
    @Transactional
    public CustomerVO updateCustomer(Long id, UpdateCustomerRequest request) {
        log.info("更新顾客信息: id={}", id);

        Customer entity = customerMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.customerNotFound();
        }

        // 名称变更时校验唯一性（排除自身）
        if (request.getName() != null && !request.getName().equals(entity.getName())) {
            LambdaQueryWrapper<Customer> nameQuery = new LambdaQueryWrapper<>();
            nameQuery.eq(Customer::getName, request.getName())
                    .ne(Customer::getId, id);
            if (customerMapper.selectCount(nameQuery) > 0) {
                throw DuplicateResourceException.duplicateCustomerName();
            }
        }

        customerConverter.updateEntity(entity, request);
        customerMapper.updateById(entity);

        // 送货地址：提供则整列表替换（先删后插）
        if (request.getShipAddresses() != null) {
            shipAddressMapper.delete(new LambdaQueryWrapper<CustomerShipAddress>()
                    .eq(CustomerShipAddress::getCustomerId, id));
            customerConverter.toShipAddressEntities(id, request.getShipAddresses())
                    .forEach(shipAddressMapper::insert);
        }
        log.info("顾客信息更新成功: id={}", id);

        Customer updated = customerMapper.selectById(id);
        return customerConverter.toCustomerVO(updated, loadAddresses(id));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("删除顾客: id={}", id);
        Customer entity = customerMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.customerNotFound();
        }
        // 物理删除送货地址子表，逻辑删除顾客主表
        shipAddressMapper.delete(new LambdaQueryWrapper<CustomerShipAddress>()
                .eq(CustomerShipAddress::getCustomerId, id));
        customerMapper.deleteById(id);
        log.info("顾客删除成功: id={}", id);
    }

    @Override
    @Transactional
    public CustomerVO updateCustomerStatus(Long id, UpdateCustomerStatusRequest request) {
        log.info("变更顾客状态: id={}, status={}", id, request.getStatus());

        Customer entity = customerMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.customerNotFound();
        }

        entity.setStatus(request.getStatus());
        customerMapper.updateById(entity);

        Customer updated = customerMapper.selectById(id);
        return customerConverter.toCustomerVO(updated, loadAddresses(id));
    }

    /** 加载单个顾客的送货地址（按 id 升序） */
    private List<CustomerShipAddress> loadAddresses(Long customerId) {
        return shipAddressMapper.selectList(new LambdaQueryWrapper<CustomerShipAddress>()
                .eq(CustomerShipAddress::getCustomerId, customerId)
                .orderByAsc(CustomerShipAddress::getId));
    }

    /** 批量加载多个顾客的送货地址，按 customerId 分组 */
    private Map<Long, List<CustomerShipAddress>> loadAddressesForCustomers(List<Long> customerIds) {
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        return shipAddressMapper.selectList(new LambdaQueryWrapper<CustomerShipAddress>()
                        .in(CustomerShipAddress::getCustomerId, customerIds)
                        .orderByAsc(CustomerShipAddress::getId))
                .stream()
                .collect(Collectors.groupingBy(CustomerShipAddress::getCustomerId));
    }
}
