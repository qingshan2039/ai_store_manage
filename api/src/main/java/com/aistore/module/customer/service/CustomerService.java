package com.aistore.module.customer.service;

import com.aistore.module.customer.dto.CreateCustomerRequest;
import com.aistore.module.customer.dto.CustomerQueryParam;
import com.aistore.module.customer.dto.UpdateCustomerRequest;
import com.aistore.module.customer.dto.UpdateCustomerStatusRequest;
import com.aistore.module.customer.vo.CustomerListResponse;
import com.aistore.module.customer.vo.CustomerVO;

/**
 * 顾客服务接口
 */
public interface CustomerService {

    CustomerVO createCustomer(CreateCustomerRequest request);

    CustomerVO getCustomerById(Long id);

    CustomerListResponse listCustomers(CustomerQueryParam param);

    CustomerVO updateCustomer(Long id, UpdateCustomerRequest request);

    void deleteCustomer(Long id);

    CustomerVO updateCustomerStatus(Long id, UpdateCustomerStatusRequest request);
}
