package top.yeelei.mall.service;

import top.yeelei.mall.controller.MallController.param.AddOrderParam;

public interface MallOrderService {
    /**
     * 生成订单
     * @param addOrderParam
     * @param userId
     * @return
     */
    String saveOrder(AddOrderParam addOrderParam, Long userId);
}
