package top.yeelei.mall.controller.admin.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddIndexConfigParam {

    @NotEmpty(message = "configName不能为空")
    private String configName;

    @NotNull(message = "configType不能为空")
    private Byte configType;

    @NotNull(message = "goodsId不能为空")
    private Long goodsId;

    private String redirectUrl;

    @NotNull(message = "configRank不能为空")
    private Integer configRank;
}