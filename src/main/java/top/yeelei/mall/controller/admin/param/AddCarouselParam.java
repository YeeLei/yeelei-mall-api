package top.yeelei.mall.controller.admin.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@Data
public class AddCarouselParam {
    @NotEmpty(message = "轮播图地址不能为空")
    private String carouselUrl;

    private String redirectUrl;

    @Min(value = 0,message = "rank值最小值为0")
    private Integer carouselRank;
}
