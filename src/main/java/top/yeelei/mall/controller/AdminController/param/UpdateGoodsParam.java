package top.yeelei.mall.controller.AdminController.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateGoodsParam {

    @NotNull(message = "商品id不能为空")
    private Long goodsId;

    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    @NotEmpty(message = "商品简介不能为空")
    private String goodsIntro;

    @NotNull(message = "商品分类id不能为空")
    private Long goodsCategoryId;

    private String goodsCoverImg;

    private String goodsCarousel;

    private Integer originalPrice;

    private Integer sellingPrice;

    private Integer stockNum;

    private String tag;

    private Byte goodsSellStatus;

    @NotNull(message = "商品详情不能为空")
    private String goodsDetailContent;

}