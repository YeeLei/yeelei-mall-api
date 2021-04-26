package top.yeelei.mall.controller.MallController;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.MallController.param.AddOrderParam;
import top.yeelei.mall.controller.MallController.vo.MallOrderDetailVO;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.service.MallOrderService;

import java.util.List;

@Api(tags = "商城订单操作相关接口")
@RequestMapping("/api")
@RestController
public class YeeLeiMallOrderApi {
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

    @GetMapping("/order/{orderNo}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public ApiRestResponse orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo,
                                           @TokenToMallUser MallUser loginMallUser) {
        MallOrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, loginMallUser.getUserId());
        return ApiRestResponse.genSuccessResult(orderDetailVO);
    }

    @PutMapping("/order/cancel/{orderNo}")
    @ApiOperation(value = "订单取消接口", notes = "传参为订单号")
    public ApiRestResponse cancelOrder(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo,
                                       @TokenToMallUser MallUser loginMallUser) {
        if (!orderService.cancelOrder(orderNo, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("订单取消失败!");
        }
        return ApiRestResponse.genSuccessResult("订单取消成功!");
    }

    @GetMapping("/order")
    @ApiOperation(value = "订单列表接口", notes = "传参为页码")
    public ApiRestResponse orderList(@ApiParam(value = "页码") @RequestParam(required = false) Integer pageNum,
                                     @ApiParam(value = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功")
                                     @RequestParam(required = false) Integer status,
                                     @TokenToMallUser MallUser loginMallUser) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        PageInfo pageInfo = orderService.getMyOrders(pageNum, status, loginMallUser.getUserId());
        return ApiRestResponse.genSuccessResult(pageInfo);
    }

    @GetMapping("/paySuccess")
    @ApiOperation(value = "模拟支付成功回调的接口", notes = "传参为订单号和支付方式")
    public ApiRestResponse paySuccess(@ApiParam(value = "订单号") @RequestParam("orderNo") String orderNo,
                                      @ApiParam(value = "支付方式") @RequestParam("payType") int payType) {
        if (!orderService.paySuccess(orderNo, payType)) {
            return ApiRestResponse.genFailResult("支付失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/order/finish/{orderNo}")
    @ApiOperation(value = "确认收货接口", notes = "传参为订单号")
    public ApiRestResponse finishOrder(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo,
                                       @TokenToMallUser MallUser loginMallUser) {
        if (!orderService.finishOrder(orderNo, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("确认收货失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }
}