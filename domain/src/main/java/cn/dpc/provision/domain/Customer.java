package cn.dpc.provision.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Customer {

    public static final Customer ANONYMOUS_CUSTOMER = new Customer();

    private CustomerId customerId;
    private String adCode;
    private CustomerLevel customerLevel;

    @AllArgsConstructor
    @Getter
    public static class CustomerId {
        String id;
    }

    public enum CustomerLevel {
        NEW_USER,
        VIP,
        SUPER_VIP;
    }
}
