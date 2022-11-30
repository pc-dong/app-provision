package cn.dpc.provision.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class Customer {

    public static final Customer ANONYMOUS_CUSTOMER = new Customer();

    protected CustomerId customerId;
    protected String adCode;
    protected CustomerLevel customerLevel;

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
