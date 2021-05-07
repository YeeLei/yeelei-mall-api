package top.yeelei.mall.model.pojo;

import lombok.Data;

@Data
public class YeeLeiMallOrderAddress {
    private Long orderId;

    private String userName;

    private String userPhone;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}