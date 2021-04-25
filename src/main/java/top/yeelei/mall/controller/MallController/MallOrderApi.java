package top.yeelei.mall.controller.MallController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.MallController.param.AddOrderParam;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.service.MallOrderService;

@Api(tags = "商城订单操作相关接口")
@RequestMapping("/api")
@RestController
public class MallOrderApi {
    @Autowired
    private MallOrderService orderService;

    @PostMapping("/saveOrder")
    @ApiOperation(value = "生成订单接口", notes = "传参为地址id和待结算的购物项id数组")
    public ApiRestResponse saveOrder(@ApiParam(value = "订单参数") @RequestBody AddOrderParam addOrderParam,
                                     @TokenToMallUser MallUser loginMallUser) {
        String orderNo = orderService.saveOrder(addOrderParam, loginMallUser.getUserId());
        if (StringUtils.isEmpty(orderNo)) {
            return ApiRestResponse.genFailResult("生成订单失败!");
        }
        Object data = orderNo;
        return ApiRestResponse.genSuccessResult(data);
    }
}
