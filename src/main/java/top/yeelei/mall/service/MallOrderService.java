package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.MallController.param.AddOrderParam;
import top.yeelei.mall.controller.MallController.vo.MallOrderDetailVO;
import top.yeelei.mall.controller.MallController.vo.MallOrderItemVO;
import top.yeelei.mall.model.pojo.YeeLeiMallOrder;

import java.util.List;

public interface MallOrderService {
    /**
     * 生成订单
     *
     * @param addOrderParam
     * @param userId
     * @return
     */
    String saveOrder(AddOrderParam addOrderParam, Long userId);

    /**
     * 根据订单号查询订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 根据订单号取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    boolean cancelOrder(String orderNo, Long userId);

    /**
     * 我的订单列表
     *
     * @param pageNum
     * @param status
     * @param userId
     * @return
     */
    PageInfo getMyOrders(Integer pageNum, Integer status, Long userId);

    /**
     * 支付成功接口
     *
     * @param orderNo
     * @param payType
     * @return
     */
    boolean paySuccess(String orderNo, int payType);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    boolean finishOrder(String orderNo, Long userId);

    /**
     * 后台订单列表分页
     *
     * @param pageNum
     * @param pageSize
     * @param orderNo
     * @param orderStatus
     * @return
     */
    PageInfo getMallOrdersPage(Integer pageNum, Integer pageSize, String orderNo, Integer orderStatus);

    /**
     * 修改订单信息
     *
     * @param yeeLeiMallOrder
     * @return
     */
    boolean updateOrderInfo(YeeLeiMallOrder yeeLeiMallOrder);

    /**
     * 获取订单项列表
     * @param orderId
     * @return
     */
    List<MallOrderItemVO> getOrderItems(Long orderId);

    /**
     * 后台获取订单详情
     * @param orderId
     * @return
     */
    MallOrderDetailVO getOrderDetailByOrderId(Long orderId);

    /**
     * 配货
     * @param ids
     * @return
     */
    boolean checkDone(Long[] ids);

    /**
     * 出库
     * @param ids
     * @return
     */
    boolean checkOut(Long[] ids);

    /**
     * 关闭订单
     * @param ids
     * @return
     */
    boolean closeOrder(Long[] ids);
}
