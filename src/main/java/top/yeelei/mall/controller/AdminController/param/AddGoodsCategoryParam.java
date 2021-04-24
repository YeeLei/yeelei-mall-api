package top.yeelei.mall.controller.AdminController.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AddGoodsCategoryParam {
    @NotNull(message = "分类等级不能为空")
    @Min(value = 1,message = "分类最大层级为1(一级分类)") //分类最大层级数
    @Max(value = 3,message = "分类最小层级为3(三级分类)") //分类最小层级数
    private Byte categoryLevel;

    @NotNull(message = "parentId不能为空")
    private Long parentId;

    @NotEmpty(message = "分类名称不能为空")
    private String categoryName;

    @NotNull(message = "排序值不能为空")
    private Integer categoryRank;
}