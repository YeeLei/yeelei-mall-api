package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallOrderItem;

import java.util.List;

@Repository
public interface YeeLeiMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(YeeLeiMallOrderItem record);

    int insertSelective(YeeLeiMallOrderItem record);

    YeeLeiMallOrderItem selectByPrimaryKey(Long orderItemId);

    int updateByPrimaryKeySelective(YeeLeiMallOrderItem record);

    int updateByPrimaryKey(YeeLeiMallOrderItem record);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<YeeLeiMallOrderItem> orderItems);
}