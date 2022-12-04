package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.Customer;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
public class RealTimeRequest {
    protected String customerId = "";
    protected String adCode = "";
    protected Customer.CustomerLevel customerLevel = null;

    public Customer parseCustomer() {
        return Customer.builder()
                .customerId(StringUtils.hasLength(customerId) ? new Customer.CustomerId(customerId) : null)
                .adCode(adCode)
                .customerLevel(customerLevel)
                .build();
    }
}
