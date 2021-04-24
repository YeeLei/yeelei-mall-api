package top.yeelei.mall.controller.AdminController.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class BatchIdParam implements Serializable {
    //id数组
    @NotEmpty(message = "id数组不能为空")
    Long[] ids;
}
