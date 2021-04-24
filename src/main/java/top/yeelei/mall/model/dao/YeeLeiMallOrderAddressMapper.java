package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallOrderAddress;

@Repository
public interface YeeLeiMallOrderAddressMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(YeeLeiMallOrderAddress record);

    int insertSelective(YeeLeiMallOrderAddress record);

    YeeLeiMallOrderAddress selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(YeeLeiMallOrderAddress record);

    int updateByPrimaryKey(YeeLeiMallOrderAddress record);
}