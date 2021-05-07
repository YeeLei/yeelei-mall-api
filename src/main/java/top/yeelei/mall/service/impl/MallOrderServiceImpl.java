package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.yeelei.mall.common.*;
import top.yeelei.mall.controller.mall.param.AddOrderParam;
import top.yeelei.mall.controller.mall.vo.*;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.*;
import top.yeelei.mall.model.pojo.*;
import top.yeelei.mall.service.MallOrderService;
import top.yeelei.mall.service.MallShoppingCartService;
import top.yeelei.mall.service.MallUserAddressService;
import top.yeelei.mall.utils.CopyListUtil;
import top.yeelei.mall.utils.NumberUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class MallOrderServiceImpl implements MallOrderService {
    @Autowired
    private MallShoppingCartService shoppingCartService;
    @Autowired
    private MallUserAddressService addressService;
    @Autowired
    private YeeLeiMallGoodsMapper mallGoodsMapper;
    @Autowired
    private YeeLeiMallShoppingCartItemMapper shoppingCartItemMapper;
    @Autowired
    private YeeLeiMallOrderMapper orderMapper;
    @Autowired
    private YeeLeiMallOrderAddressMapper orderAddressMapper;
    @Autowired
    private YeeLeiMallOrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public String saveOrder(AddOrderParam addOrderParam, Long userId) {
        int priceTotal = 0;
        if (addOrderParam == null || addOrderParam.getCartItemIds() == null || addOrderParam.getAddressId() == null) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (addOrderParam.getCartItemIds().length < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<MallShoppingCartItemVO> myShoppingCartItems =
                shoppingCartService.getCartItemsForSettle(Arrays.asList(addOrderParam.getCartItemIds()), userId);
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        } else {
            //计算总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += mallShoppingCartItemVO.getTotalPrice();
            }
            if (priceTotal < 1) {
                throw new YeeLeiMallException(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
            }
            MallUserAddressVO address = addressService.getUserAddressById(addOrderParam.getAddressId());
            //判断所属
            if (!address.getUserId().equals(userId)) {
                //地址不是所属用户
                throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            //保存订单并返回订单号
            //获取所有购物项id,并保存到集合中
            List<Long> itemIdList = myShoppingCartItems.stream()
                    .map(MallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
            //获取所有购物项的商品id,并保存到集合中
            List<Long> goodsIdList = myShoppingCartItems.stream()
                    .map(MallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
            //根据goodsIdList查询所有的商品
            List<YeeLeiMallGoods> goodsList = mallGoodsMapper.selectByPrimaryKeys(goodsIdList);
            //检查是否包含已下架商品
            //筛选出所有没有上架的商品集合
            List<YeeLeiMallGoods> goodsListNotSelling = goodsList.stream().filter(goodsTemp ->
                    goodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
                //goodsListNotSelling 对象非空则表示有下架商品
                throw new YeeLeiMallException(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
            }
            //生成goodsId对应的GoodsList的Map集合
            Map<Long, YeeLeiMallGoods> goodsMap = goodsList.stream().collect(Collectors
                    .toMap(YeeLeiMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            //判断商品库存
            for (MallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
                if (!goodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                    throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
                }
                //存在数量大于库存的情况，直接返回错误提醒
                if (shoppingCartItemVO.getGoodsCount() > goodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                    throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
            }
            //删除购物项
            if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIdList) &&
                    !CollectionUtils.isEmpty(goodsList)) {
                if (shoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                    List<StockNumDTO> stockNumDTOS =
                            CopyListUtil.copyListProperties(myShoppingCartItems, StockNumDTO::new);
                    //更新库存数量
                    if (mallGoodsMapper.updateStockNum(stockNumDTOS) < 1) {
                        //库存不足
                        throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                    }
                    //生成订单号
                    String orderNo = NumberUtil.genOrderNo();
                    //保存订单
                    YeeLeiMallOrder mallOrder = new YeeLeiMallOrder();
                    mallOrder.setOrderNo(orderNo);
                    mallOrder.setUserId(userId);
                    mallOrder.setTotalPrice(priceTotal);
                    String extraInfo = "";
                    mallOrder.setExtraInfo(extraInfo);
                    //生成订单项并保存订单项记录
                    if (orderMapper.insertSelective(mallOrder) > 0) {
                        //生成订单收货地址快照，并保存至数据库
                        YeeLeiMallOrderAddress orderAddress = new YeeLeiMallOrderAddress();
                        BeanUtils.copyProperties(address, orderAddress);
                        orderAddress.setOrderId(mallOrder.getOrderId());
                        //生成所有的订单项快照，并保存至数据库
                        List<YeeLeiMallOrderItem> mallOrderItems = new ArrayList<>();
                        for (MallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                            YeeLeiMallOrderItem mallOrderItem = new YeeLeiMallOrderItem();
                            BeanUtils.copyProperties(myShoppingCartItem, mallOrderItem);
                            //YeeLeiMallOrderMapper文件insertSelective()方法中使用了useGeneratedKeys因此orderId可以获取到
                            mallOrderItem.setOrderId(mallOrder.getOrderId());
                            mallOrderItems.add(mallOrderItem);
                        }
                        //保存订单项快照至数据库
                        if (orderItemMapper.insertBatch(mallOrderItems) > 0 && orderAddressMapper.insertSelective(orderAddress) > 0) {
                            //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                            return orderNo;
                        }
                    }
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }
                throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
            }
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
    }

    @Override
    public MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //判断用户所属
        if (!yeeLeiMallOrder.getUserId().equals(userId)) {
            //不是当前用户的订单
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<YeeLeiMallOrderItem> mallOrderItemList = orderItemMapper.selectByOrderId(yeeLeiMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(mallOrderItemList)) {
            List<MallOrderItemVO> mallOrderItemVOS = CopyListUtil.copyListProperties(mallOrderItemList, MallOrderItemVO::new);
            //创建新的订单详情
            MallOrderDetailVO orderDetailVO = new MallOrderDetailVO();
            BeanUtils.copyProperties(yeeLeiMallOrder, orderDetailVO);
            orderDetailVO.setOrderStatusString(YeeLeiMallOrderStatusEnum.getYeeLeiMallOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
            orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
            orderDetailVO.setMallOrderItemVOS(mallOrderItemVOS);
            return orderDetailVO;
        }
        throw new YeeLeiMallException(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
    }

    @Override
    public boolean cancelOrder(String orderNo, Long userId) {
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //判断用户所属
        if (!yeeLeiMallOrder.getUserId().equals(userId)) {
            //不是当前用户的订单
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        if (orderMapper.closeOrder(Collections.singletonList(yeeLeiMallOrder.getOrderId()),
                YeeLeiMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo getMyOrders(Integer pageNum, Integer status, Long userId) {
        int total = orderMapper.getTotalMallOrders(status, userId);
        PageHelper.startPage(pageNum, Constants.ORDER_SEARCH_PAGE_LIMIT);
        List<YeeLeiMallOrder> mallOrderList = orderMapper.findMallOrderList(status, userId);
        List<MallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = CopyListUtil.copyListProperties(mallOrderList, MallOrderListVO::new);
            //设置订单状态中文显示值
            for (MallOrderListVO mallOrderListVO : orderListVOS) {
                mallOrderListVO.setOrderStatusString(YeeLeiMallOrderStatusEnum
                        .getYeeLeiMallOrderStatusEnumByStatus(mallOrderListVO.getOrderStatus()).getName());
            }
            //获取所有的orderIds列表
            List<Long> orderIds = mallOrderList.stream()
                    .map(YeeLeiMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                //根据orderIds查询所有的订单项列表
                List<YeeLeiMallOrderItem> mallOrderItemList = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<YeeLeiMallOrderItem>> itemByOrderIdMap =
                        mallOrderItemList.stream().collect(groupingBy(YeeLeiMallOrderItem::getOrderId));
                for (MallOrderListVO orderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(orderListVO.getOrderId())) {
                        List<YeeLeiMallOrderItem> mallOrderItemListTemp =
                                itemByOrderIdMap.get(orderListVO.getOrderId());
                        //将YeeLeiMallOrderItem对象列表转换成YeeLeiMallOrderItemVO对象列表
                        List<MallOrderItemVO> mallOrderItemVOS = CopyListUtil
                                .copyListProperties(mallOrderItemListTemp, MallOrderItemVO::new);
                        orderListVO.setMallOrderItemVOS(mallOrderItemVOS);
                    }
                }
            }
        }
        PageInfo pageInfo = new PageInfo<>(mallOrderList);
        pageInfo.setList(orderListVOS);
        return pageInfo;
    }

    @Override
    public boolean paySuccess(String orderNo, int payType) {
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        if (yeeLeiMallOrder.getOrderStatus().intValue() != YeeLeiMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            throw new YeeLeiMallException("非待支付状态下的订单无法支付");
        }
        yeeLeiMallOrder.setOrderStatus((byte) YeeLeiMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
        yeeLeiMallOrder.setPayType((byte) payType);
        yeeLeiMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
        yeeLeiMallOrder.setPayTime(new Date());
        yeeLeiMallOrder.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKeySelective(yeeLeiMallOrder) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean finishOrder(String orderNo, Long userId) {
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByOrderNo(orderNo);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        if (yeeLeiMallOrder.getPayStatus().intValue() == YeeLeiMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            throw new YeeLeiMallException("待支付状态下的订单无法进行确认收货");
        }
        yeeLeiMallOrder.setOrderStatus((byte) YeeLeiMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
        yeeLeiMallOrder.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKeySelective(yeeLeiMallOrder) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo getMallOrdersPage(Integer pageNum, Integer pageSize, String orderNo, Integer orderStatus) {
        PageHelper.startPage(pageNum, pageSize);
        List<YeeLeiMallOrder> orderList = orderMapper.findMallOrderListForAdmin(orderNo, orderStatus);
        PageInfo<YeeLeiMallOrder> pageInfo = new PageInfo<>(orderList);
        return pageInfo;
    }

    @Override
    public boolean updateOrderInfo(YeeLeiMallOrder yeeLeiMallOrder) {
        YeeLeiMallOrder order = orderMapper.selectByPrimaryKey(yeeLeiMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (order != null && order.getOrderStatus() >= 0 && order.getOrderStatus() < 3) {
            order.setTotalPrice(yeeLeiMallOrder.getTotalPrice());
            order.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(order) > 0) {
                return true;
            }
        } else {
            throw new YeeLeiMallException(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return false;
    }

    @Override
    public List<MallOrderItemVO> getOrderItems(Long orderId) {
        if (orderId == null || orderId < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByPrimaryKey(orderId);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<YeeLeiMallOrderItem> mallOrderItems = orderItemMapper.selectByOrderId(orderId);
        //获取订单项数据
        if (!CollectionUtils.isEmpty(mallOrderItems)) {
            List<MallOrderItemVO> orderItemVOS =
                    CopyListUtil.copyListProperties(mallOrderItems, MallOrderItemVO::new);
            return orderItemVOS;
        }
        return null;
    }

    @Override
    public MallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        YeeLeiMallOrder yeeLeiMallOrder = orderMapper.selectByPrimaryKey(orderId);
        if (yeeLeiMallOrder == null) {
            throw new YeeLeiMallException(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<YeeLeiMallOrderItem> mallOrderItems = orderItemMapper.selectByOrderId(yeeLeiMallOrder.getOrderId());

        if (CollectionUtils.isEmpty(mallOrderItems)) {
            throw new YeeLeiMallException(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
        }
        //获取订单项数据
        List<MallOrderItemVO> orderItemVOS =
                CopyListUtil.copyListProperties(mallOrderItems, MallOrderItemVO::new);
        MallOrderDetailVO orderDetailVO = new MallOrderDetailVO();
        BeanUtils.copyProperties(yeeLeiMallOrder, orderDetailVO);
        orderDetailVO.setMallOrderItemVOS(orderItemVOS);
        orderDetailVO.setOrderStatusString(YeeLeiMallOrderStatusEnum
                .getYeeLeiMallOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
        orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
        return orderDetailVO;
    }

    @Override
    @Transactional
    public boolean checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<YeeLeiMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (YeeLeiMallOrder yeeLeiMallOrder : orders) {
                if (yeeLeiMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += yeeLeiMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (yeeLeiMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += yeeLeiMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return true;
                } else {
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }

            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    throw new YeeLeiMallException(errorOrderNos + "订单的状态不是支付成功无法执行出库操作");
                } else {
                    throw new YeeLeiMallException("你选择了太多状态不是支付成功的订单，无法执行配货完成操作");
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<YeeLeiMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (YeeLeiMallOrder yeeLeiMallOrder : orders) {
                if (yeeLeiMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += yeeLeiMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (yeeLeiMallOrder.getOrderStatus() != 1 && yeeLeiMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += yeeLeiMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return true;
                } else {
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    throw new YeeLeiMallException(errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作");
                } else {
                    throw new YeeLeiMallException("你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作");
                }
            }
        }
        return false;
    }

    @Override
    public boolean closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<YeeLeiMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (YeeLeiMallOrder order : orders) {
                // isDeleted=1 一定为已关闭订单
                if (order.getIsDeleted() == -1) {
                    errorOrderNos += order.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (order.getOrderStatus() == 4 || order.getOrderStatus() < 0) {
                    errorOrderNos += order.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderMapper.closeOrder(Arrays.asList(ids), YeeLeiMallOrderStatusEnum
                        .ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return true;
                } else {
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    throw new YeeLeiMallException(errorOrderNos + "订单不能执行关闭操作");
                } else {
                    throw new YeeLeiMallException("你选择的订单不能执行关闭操作");
                }
            }
        }
        return false;
    }
}
