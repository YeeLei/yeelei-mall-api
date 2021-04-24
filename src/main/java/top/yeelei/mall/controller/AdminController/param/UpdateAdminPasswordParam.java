package top.yeelei.mall.controller.AdminController.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateAdminPasswordParam {

    @ApiModelProperty("管理员原密码")
    private String originalPassword;

    @ApiModelProperty("管理员新密码")
    private String newPassword;
}
