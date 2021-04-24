package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallShoppingCartItem;

import java.util.List;

@Repository
public interface YeeLeiMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(YeeLeiMallShoppingCartItem record);

    int insertSelective(YeeLeiMallShoppingCartItem record);

    YeeLeiMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(YeeLeiMallShoppingCartItem record);

    int updateByPrimaryKey(YeeLeiMallShoppingCartItem record);

    YeeLeiMallShoppingCartItem selectByUserIdAndGoodsId(@Param("userId") Long userId,
                                                        @Param("goodsId") Long goodsId);

    int selectCountByUserId(@Param("userId") Long userId);

    List<YeeLeiMallShoppingCartItem> selectByUserIdAndCartItemIds(@Param("userId") Long userId,
                                                                  @Param("itemIds") List<Long> itemIds);

    List<YeeLeiMallShoppingCartItem> findMyMallCartItems(@Param("userId") Long userId);

    List<YeeLeiMallShoppingCartItem> selectByUserId(@Param("userId") Long userId,
                                                    @Param("number") Integer number);
}