package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallOrder;

@Repository
public interface YeeLeiMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(YeeLeiMallOrder record);

    int insertSelective(YeeLeiMallOrder record);

    YeeLeiMallOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(YeeLeiMallOrder record);

    int updateByPrimaryKey(YeeLeiMallOrder record);
}