package top.yeelei.mall.controller.AdminController;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.AdminController.param.BatchIdParam;
import top.yeelei.mall.controller.MallController.vo.MallOrderItemVO;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.YeeLeiMallOrder;
import top.yeelei.mall.service.MallOrderService;

import java.util.List;
import java.util.Objects;

@RestController
@Api(tags = "后台管理系统订单模块接口")
@RequestMapping("/manage-api")
public class AdminOrderApi {
    @Autowired
    private MallOrderService orderService;

    @GetMapping("/orders")
    @ApiOperation(value = "订单列表", notes = "可根据订单号和订单状态筛选")
    public ApiRestResponse list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                                @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                                @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 10) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        PageInfo pageInfo = orderService.getMallOrdersPage(pageNum, pageSize, orderNo, orderStatus);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }

    @PutMapping("/orders")
    @ApiOperation(value = "修改订单价格", notes = "修改订单价格")
    public ApiRestResponse update(@RequestBody YeeLeiMallOrder yeeLeiMallOrder, @TokenToAdminUser AdminUserToken adminUser) {
        if (Objects.isNull(yeeLeiMallOrder.getTotalPrice())
                || Objects.isNull(yeeLeiMallOrder.getOrderId())
                || yeeLeiMallOrder.getOrderId() < 1
                || yeeLeiMallOrder.getTotalPrice() < 1) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        if (!orderService.updateOrderInfo(yeeLeiMallOrder)) {
            return ApiRestResponse.genFailResult("更新失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/orderItems/{orderId}")
    @ApiOperation(value = "获取订单项数据", notes = "根据id查询")
    public ApiRestResponse info(@PathVariable("orderId") Long orderId, @TokenToAdminUser AdminUserToken adminUser) {
        List<MallOrderItemVO> orderItemVOS = orderService.getOrderItems(orderId);
        return ApiRestResponse.genSuccessResult(orderItemVOS);
    }

    @GetMapping("/orders/{orderId}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public ApiRestResponse orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderId")
                                                   Long orderId,
                                           @TokenToAdminUser AdminUserToken adminUser) {
        return ApiRestResponse.genSuccessResult(orderService.getOrderDetailByOrderId(orderId));
    }

    @PutMapping("/orders/checkDone")
    @ApiOperation(value = "修改订单状态为配货成功", notes = "批量修改")
    public ApiRestResponse checkDone(@RequestBody BatchIdParam batchIdParam,
                                     @TokenToAdminUser AdminUserToken adminUser) {
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        if (!orderService.checkDone(batchIdParam.getIds())) {
            return ApiRestResponse.genFailResult("配货失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/orders/checkOut")
    @ApiOperation(value = "修改订单状态为已出库", notes = "批量修改")
    public ApiRestResponse checkOut(@RequestBody BatchIdParam batchIdParam,
                                    @TokenToAdminUser AdminUserToken adminUser) {
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        if (!orderService.checkOut(batchIdParam.getIds())) {
            return ApiRestResponse.genFailResult("出库失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/orders/close")
    @ApiOperation(value = "修改订单状态为商家关闭", notes = "批量修改")
    public ApiRestResponse closeOrder(@RequestBody BatchIdParam batchIdParam,
                                      @TokenToAdminUser AdminUserToken adminUser) {
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        if (!orderService.closeOrder(batchIdParam.getIds())) {
            return ApiRestResponse.genFailResult("关闭失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }
}

