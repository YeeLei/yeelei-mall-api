package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.mall.param.AddCartItemParam;
import top.yeelei.mall.controller.mall.param.UpdateCartItemParam;
import top.yeelei.mall.controller.mall.vo.MallShoppingCartItemVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.YeeLeiMallGoodsMapper;
import top.yeelei.mall.model.dao.YeeLeiMallShoppingCartItemMapper;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;
import top.yeelei.mall.model.pojo.YeeLeiMallShoppingCartItem;
import top.yeelei.mall.service.MallShoppingCartService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MallShoppingCartServiceImpl implements MallShoppingCartService {
    @Autowired
    private YeeLeiMallShoppingCartItemMapper shoppingCartItemMapper;
    @Autowired
    private YeeLeiMallGoodsMapper goodsMapper;

    @Override
    public boolean addMallShoppingCartItem(AddCartItemParam addCartItemParam, Long userId) {
        YeeLeiMallShoppingCartItem cartItem =
                shoppingCartItemMapper.selectByUserIdAndGoodsId(userId, addCartItemParam.getGoodsId());
        if (cartItem != null) {
            //已存在则修改该记录
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        YeeLeiMallGoods goods = goodsMapper.selectByPrimaryKey(addCartItemParam.getGoodsId());
        if (goods == null) {
            //没找到该商品
            throw new YeeLeiMallException(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }

        if (addCartItemParam.getGoodsCount() < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult());
        }

        if (addCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            //超出单个商品的最大购买数量
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult());
        }
        int totalItem = shoppingCartItemMapper.selectCountByUserId(userId);
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            //超出当前用户购买的最大商品种类数量
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult());
        }

        YeeLeiMallShoppingCartItem newShoppingCartItem = new YeeLeiMallShoppingCartItem();
        BeanUtils.copyProperties(addCartItemParam, newShoppingCartItem);
        newShoppingCartItem.setUserId(userId);
        //保存记录
        if (shoppingCartItemMapper.insertSelective(newShoppingCartItem) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        YeeLeiMallShoppingCartItem cartItem = shoppingCartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (cartItem == null) {
            throw new YeeLeiMallException(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        //验证用户身份
        if (!cartItem.getUserId().equals(userId)) {
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }

        //超出单个商品的最大数量
        if (updateCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult());
        }
        cartItem.setGoodsCount(updateCartItemParam.getGoodsCount());
        cartItem.setUpdateTime(new Date());

        //更新
        if (shoppingCartItemMapper.updateByPrimaryKeySelective(cartItem) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByIdAndUserId(Long shoppingCartItemId, Long userId) {
        YeeLeiMallShoppingCartItem cartItem = shoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (cartItem == null) {
            throw new YeeLeiMallException(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        //验证用户身份
        if (!cartItem.getUserId().equals(userId)) {
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        //删除购物项
        if (shoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<MallShoppingCartItemVO> getCartItemsForSettle(List<Long> itemIds, Long userId) {
        if (itemIds.size() < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = new ArrayList<>();

        if (CollectionUtils.isEmpty(itemIds)) {
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_NOT_NULL.getResult());
        }
        List<YeeLeiMallShoppingCartItem> mallShoppingCartItems =
                shoppingCartItemMapper.selectByUserIdAndCartItemIds(userId, itemIds);
        if (CollectionUtils.isEmpty(mallShoppingCartItems)) {
            throw new YeeLeiMallException(ServiceResultEnum.SHOPPING_CART_ITEM_IS_NULL.getResult());
        }

        if (mallShoppingCartItems.size() != itemIds.size()) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }

        return getMallShoppingCartItemVOS(mallShoppingCartItemVOS, mallShoppingCartItems);
    }

    @Override
    public PageInfo getMyShoppingCartItems(Integer pageNum, Long userId) {
        PageHelper.startPage(pageNum, Constants.SHOPPING_CART_PAGE_LIMIT, "create_time desc");
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = new ArrayList<>();
        List<YeeLeiMallShoppingCartItem> mallShoppingCartItems = shoppingCartItemMapper.findMyMallCartItems(userId);
        List<MallShoppingCartItemVO> shoppingCartItemVOS = getMallShoppingCartItemVOS(mallShoppingCartItemVOS, mallShoppingCartItems);
        PageInfo pageInfo = new PageInfo<>(mallShoppingCartItems);
        pageInfo.setList(shoppingCartItemVOS);
        return pageInfo;
    }

    @Override
    public List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = new ArrayList<>();
        List<YeeLeiMallShoppingCartItem> mallShoppingCartItems =
                shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getMallShoppingCartItemVOS(mallShoppingCartItemVOS, mallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param mallShoppingCartItemVOS
     * @param mallShoppingCartItems
     * @return
     */
    private List<MallShoppingCartItemVO> getMallShoppingCartItemVOS(List<MallShoppingCartItemVO> mallShoppingCartItemVOS,
                                                                    List<YeeLeiMallShoppingCartItem> mallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(mallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> mallGoodsIds = mallShoppingCartItems.stream().
                    map(YeeLeiMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<YeeLeiMallGoods> mallGoodsList = goodsMapper.selectByPrimaryKeys(mallGoodsIds);
            Map<Long, YeeLeiMallGoods> mallGoodsHashMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(mallGoodsList)) {
                //如果mallGoodsList不为空
                //对商品按照goodsId进行分组
                mallGoodsHashMap = mallGoodsList.stream().collect(Collectors.toMap(YeeLeiMallGoods::getGoodsId,
                        Function.identity(), (entity1, entity2) -> entity1));
            }
            for (YeeLeiMallShoppingCartItem mallShoppingCartItem : mallShoppingCartItems) {
                MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                BeanUtils.copyProperties(mallShoppingCartItem, mallShoppingCartItemVO);
                if (mallGoodsHashMap.containsKey(mallShoppingCartItem.getGoodsId())) {
                    YeeLeiMallGoods mallGoodsTemp = mallGoodsHashMap.get(mallShoppingCartItem.getGoodsId());
                    mallShoppingCartItemVO.setGoodsCoverImg(mallGoodsTemp.getGoodsCoverImg());
                    String goodsName = mallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    mallShoppingCartItemVO.setGoodsName(goodsName);
                    mallShoppingCartItemVO.setSellingPrice(mallGoodsTemp.getSellingPrice());
                    mallShoppingCartItemVOS.add(mallShoppingCartItemVO);
                }
            }
        }
        if (CollectionUtils.isEmpty(mallShoppingCartItemVOS)) {
            //无数据则抛出异常
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        } else {
            //计算总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : mallShoppingCartItemVOS) {
                int priceTotal = 0;
                priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
                if (priceTotal < 1) {
                    throw new YeeLeiMallException("价格异常");
                }
                mallShoppingCartItemVO.setTotalPrice(priceTotal);
            }
        }
        return mallShoppingCartItemVOS;
    }
}
