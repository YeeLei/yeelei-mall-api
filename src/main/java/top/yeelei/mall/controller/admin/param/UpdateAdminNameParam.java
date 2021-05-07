package top.yeelei.mall.controller.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAdminNameParam {

    @ApiModelProperty("登录名称")
    @NotEmpty(message = "登录名称不能为空")
    private String loginUserName;

    @ApiModelProperty("管理员昵称")
    @NotEmpty(message = "昵称不能为空")
    private String nickName;
}
