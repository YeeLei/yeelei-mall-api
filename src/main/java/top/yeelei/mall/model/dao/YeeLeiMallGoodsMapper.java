package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.StockNumDTO;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;

import java.util.List;

@Repository
public interface YeeLeiMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(YeeLeiMallGoods record);

    int insertSelective(YeeLeiMallGoods record);

    YeeLeiMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(YeeLeiMallGoods record);

    int updateByPrimaryKeyWithBLOBs(YeeLeiMallGoods record);

    int updateByPrimaryKey(YeeLeiMallGoods record);

    int batchUpdateSellStatus(@Param("sellStatus") Long sellStatus,
                              @Param("ids") Long[] ids,
                              @Param("userId") Long userId);

    List<YeeLeiMallGoods> listGoodsForAdmin(@Param("goodsName") String goodsName,
                                            @Param("goodsSellStatus") Integer goodsSellStatus);

    List<YeeLeiMallGoods> selectMallGoodsListBySearch(@Param("keyword") String keyword,
                                                      @Param("goodsCategoryId") Long goodsCategoryId,
                                                      @Param("orderBy") String orderBy,
                                                      @Param("goodsSellStatus") int goodsSellStatus);

    List<YeeLeiMallGoods> selectByPrimaryKeys(List<Long> goodIds);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}