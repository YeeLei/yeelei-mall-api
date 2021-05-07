package top.yeelei.mall.controller.admin.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class BatchIdParam implements Serializable {
    //id数组
    @NotEmpty(message = "id数组不能为空")
    Long[] ids;
}
