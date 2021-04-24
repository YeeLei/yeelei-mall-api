package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.MallUserAddress;

import java.util.List;

@Repository
public interface MallUserAddressMapper {
    int deleteByPrimaryKey(Long addressId);

    int insert(MallUserAddress record);

    int insertSelective(MallUserAddress record);

    MallUserAddress selectByPrimaryKey(Long addressId);

    int updateByPrimaryKeySelective(MallUserAddress record);

    int updateByPrimaryKey(MallUserAddress record);

    MallUserAddress getMyDefaultAddress(@Param("userId") Long userId);

    MallUserAddress findDefaultUserAddressByUserId(Long userId);

    List<MallUserAddress> getMyAddressList(Long userId);
}