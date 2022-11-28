package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.Customer;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class RealTimeRequest {
    protected String customerId;
    protected String adCode;
    protected Customer.CustomerLevel customerLevel;

    public Customer parseCustomer() {
        return Customer.builder()
                .customerId(StringUtils.hasLength(customerId) ? new Customer.CustomerId(customerId) : null)
                .adCode(adCode)
                .customerLevel(customerLevel)
                .build();
    }
}
