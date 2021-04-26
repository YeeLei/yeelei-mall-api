package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.YeeLeiMallOrder;

import java.util.List;

@Repository
public interface YeeLeiMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(YeeLeiMallOrder record);

    int insertSelective(YeeLeiMallOrder record);

    YeeLeiMallOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(YeeLeiMallOrder record);

    int updateByPrimaryKey(YeeLeiMallOrder record);

    YeeLeiMallOrder selectByOrderNo(String orderNo);

    int closeOrder(@Param("orderIds") List<Long> orderIds,@Param("orderStatus") int orderStatus);

    int getTotalMallOrders(@Param("orderStatus") Integer orderStatus,@Param("userId") Long userId);

    List<YeeLeiMallOrder> findMallOrderList(@Param("orderStatus") Integer orderStatus, @Param("userId") Long userId);
}