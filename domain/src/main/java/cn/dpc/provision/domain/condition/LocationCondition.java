package cn.dpc.provision.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * adCode 表示地理位置编号
 * 一共六位数字前两位代表省、中间两位代表市、后两位代表区
 * */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationCondition {
    private List<String> adCodes;
}
