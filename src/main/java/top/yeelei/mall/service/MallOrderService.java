package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.MallController.param.AddOrderParam;
import top.yeelei.mall.controller.MallController.vo.MallOrderDetailVO;

public interface MallOrderService {
    /**
     * 生成订单
     * @param addOrderParam
     * @param userId
     * @return
     */
    String saveOrder(AddOrderParam addOrderParam, Long userId);

    /**
     * 根据订单号查询订单详情
     * @param orderNo
     * @param userId
     * @return
     */
    MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 根据订单号取消订单
     * @param orderNo
     * @param userId
     * @return
     */
    boolean cancelOrder(String orderNo, Long userId);

    /**
     * 我的订单列表
     * @param pageNum
     * @param status
     * @param userId
     * @return
     */
    PageInfo getMyOrders(Integer pageNum, Integer status, Long userId);

    /**
     * 支付成功接口
     * @param orderNo
     * @param payType
     * @return
     */
    boolean paySuccess(String orderNo, int payType);

    /**
     * 确认收货
     * @param orderNo
     * @param userId
     * @return
     */
    boolean finishOrder(String orderNo, Long userId);
}
