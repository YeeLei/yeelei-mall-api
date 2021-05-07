package top.yeelei.mall.service;

import top.yeelei.mall.controller.mall.param.AddMallUserAddressParam;
import top.yeelei.mall.controller.mall.param.UpdateMallUserAddressParam;
import top.yeelei.mall.controller.mall.vo.MallUserAddressVO;
import top.yeelei.mall.model.pojo.MallUserAddress;

import java.util.List;

public interface MallUserAddressService {
    /**
     * 添加用户地址信息
     * @param addMallUserAddressParam
     * @param userId
     * @return
     */
    boolean addUserAddress(AddMallUserAddressParam addMallUserAddressParam, Long userId);

    /**
     * 根据addressId获取用户地址详情
     * @param addressId
     * @return
     */
    MallUserAddressVO getUserAddressById(Long addressId);

    /**
     * 更新用户地址信息
     * @param updateMallUserAddressParam
     * @param userId
     * @return
     */
    boolean updateMallUserAddress(UpdateMallUserAddressParam updateMallUserAddressParam, Long userId);

    /**
     * 获取默认用户地址信息
     * @param userId
     * @return
     */
    MallUserAddress getDefaultMallUserAddress(Long userId);

    /**
     * 根据addressId和userId删除地址信息
     * @param addressId
     * @param userId
     * @return
     */
    boolean deleteAddressById(Long addressId, Long userId);

    /**
     * 获取我的地址信息列表
     * @param userId
     * @return
     */
    List<MallUserAddressVO> getMyAddressList(Long userId);
}
