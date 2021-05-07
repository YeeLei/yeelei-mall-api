package top.yeelei.mall.controller.admin.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class UpdateCarouselParam {
    @NotNull(message = "carouselId不能为空")
    private Integer carouselId;

    @NotEmpty(message = "轮播图地址不能为空")
    private String carouselUrl;

    private String redirectUrl;

    @Min(value = 0, message = "rank值最小值为0")
    private Integer carouselRank;
}
