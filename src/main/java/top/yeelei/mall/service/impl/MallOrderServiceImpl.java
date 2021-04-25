package top.yeelei.mall.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.MallController.param.AddOrderParam;
import top.yeelei.mall.controller.MallController.vo.MallShoppingCartItemVO;
import top.yeelei.mall.controller.MallController.vo.MallUserAddressVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.*;
import top.yeelei.mall.model.pojo.*;
import top.yeelei.mall.service.MallOrderService;
import top.yeelei.mall.service.MallShoppingCartService;
import top.yeelei.mall.service.MallUserAddressService;
import top.yeelei.mall.utils.CopyListUtil;
import top.yeelei.mall.utils.NumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        if (addOrderParam == null || addOrderParam.getCartItemIds() == null ||
                addOrderParam.getAddressId() == null) {
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
            //判断所属
            MallUserAddressVO address = addressService.getUserAddressById(addOrderParam.getAddressId());
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
                        //保存至数据库
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
}
