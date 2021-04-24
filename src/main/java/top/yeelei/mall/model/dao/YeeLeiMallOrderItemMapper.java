package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallOrderItem;

@Repository
public interface YeeLeiMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(YeeLeiMallOrderItem record);

    int insertSelective(YeeLeiMallOrderItem record);

    YeeLeiMallOrderItem selectByPrimaryKey(Long orderItemId);

    int updateByPrimaryKeySelective(YeeLeiMallOrderItem record);

    int updateByPrimaryKey(YeeLeiMallOrderItem record);
}