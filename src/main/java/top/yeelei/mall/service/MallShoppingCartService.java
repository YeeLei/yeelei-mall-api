package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.MallController.param.AddCartItemParam;
import top.yeelei.mall.controller.MallController.param.UpdateCartItemParam;
import top.yeelei.mall.controller.MallController.vo.MallShoppingCartItemVO;

import java.util.List;

public interface MallShoppingCartService {
    /**
     * 添加商品至购物车中
     *
     * @param addCartItemParam
     * @param userId
     * @return
     */
    boolean addMallShoppingCartItem(AddCartItemParam addCartItemParam, Long userId);

    /**
     * 更新购物车中的属性
     *
     * @param updateCartItemParam
     * @param userId
     * @return
     */
    boolean updateMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId);

    /**
     * 删除购物车中的商品
     *
     * @param shoppingCartItemId
     * @param userId
     * @return
     */
    boolean deleteByIdAndUserId(Long shoppingCartItemId, Long userId);

    /**
     * 根据userId和cartItemIds获取对应的购物项记录
     *
     * @param itemIds
     * @param userId
     * @return
     */
    List<MallShoppingCartItemVO> getCartItemsForSettle(List<Long> itemIds, Long userId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param pageNum
     * @param userId
     * @return
     */
    PageInfo getMyShoppingCartItems(Integer pageNum, Long userId);

    /**
     * 我的购物车(分页数据)
     *
     * @param userId
     * @return
     */
    List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId);
}
