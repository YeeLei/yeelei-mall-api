package top.yeelei.mall.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.mall.param.AddMallUserAddressParam;
import top.yeelei.mall.controller.mall.param.UpdateMallUserAddressParam;
import top.yeelei.mall.controller.mall.vo.MallUserAddressVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.MallUserAddressMapper;
import top.yeelei.mall.model.pojo.MallUserAddress;
import top.yeelei.mall.service.MallUserAddressService;
import top.yeelei.mall.utils.CopyListUtil;

import java.util.Date;
import java.util.List;

@Service
public class MallUserAddressServiceImpl implements MallUserAddressService {
    @Autowired
    private MallUserAddressMapper userAddressMapper;

    @Override
    public boolean addUserAddress(AddMallUserAddressParam addMallUserAddressParam, Long userId) {
        MallUserAddress userAddress = new MallUserAddress();
        BeanUtils.copyProperties(addMallUserAddressParam, userAddress);
        userAddress.setUserId(userId);
        //判断用户新增地址是否为默认地址
        if (addMallUserAddressParam.getDefaultFlag() == 1) {
            //是默认地址，则需要将原有的默认地址修改掉
            MallUserAddress defaultAddress = userAddressMapper.getMyDefaultAddress(userId);
            if (defaultAddress != null) {
                defaultAddress.setDefaultFlag((byte) 0);
                defaultAddress.setUpdateTime(new Date());
                //更新地址
                if (userAddressMapper.updateByPrimaryKeySelective(defaultAddress) < 1) {
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }
                //插入新的地址
                if (userAddressMapper.insertSelective(userAddress) > 0) {
                    return true;
                }
            }
        }
        //不是默认地址，则直接插入新数据
        if (userAddressMapper.insertSelective(userAddress) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public MallUserAddressVO getUserAddressById(Long addressId) {
        MallUserAddress userAddress = userAddressMapper.selectByPrimaryKey(addressId);
        if (userAddress == null) {
            throw new YeeLeiMallException("获取用户地址失败!");
        }
        MallUserAddressVO mallUserAddressVO = new MallUserAddressVO();
        BeanUtils.copyProperties(userAddress, mallUserAddressVO);
        return mallUserAddressVO;
    }

    @Override
    public boolean updateMallUserAddress(UpdateMallUserAddressParam updateMallUserAddressParam, Long userId) {
        MallUserAddress userAddress = userAddressMapper.selectByPrimaryKey(updateMallUserAddressParam.getAddressId());
        if (!userAddress.getUserId().equals(userId)) {
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        MallUserAddress newMallUserAddress = new MallUserAddress();
        BeanUtils.copyProperties(updateMallUserAddressParam, newMallUserAddress);
        newMallUserAddress.setUserId(userId);

        //根据传入的addressId查询地址信息
        MallUserAddress oldMallUserAddress = userAddressMapper.selectByPrimaryKey(newMallUserAddress.getAddressId());
        if (newMallUserAddress.getDefaultFlag() == 1) {
            //修改为默认地址，需要将原有的默认地址修改掉
            MallUserAddress defaultAddress = userAddressMapper.getMyDefaultAddress(newMallUserAddress.getUserId());
            if (defaultAddress != null && !defaultAddress.getAddressId().equals(oldMallUserAddress.getAddressId())) {
                //存在默认地址且默认地址并不是当前修改的地址
                defaultAddress.setDefaultFlag((byte) 0);
                defaultAddress.setUpdateTime(new Date());
                if (userAddressMapper.updateByPrimaryKeySelective(defaultAddress) < 1) {
                    //更新失败
                    throw new YeeLeiMallException(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        newMallUserAddress.setUpdateTime(new Date());
        return userAddressMapper.updateByPrimaryKeySelective(newMallUserAddress) > 0;
    }

    @Override
    public MallUserAddress getDefaultMallUserAddress(Long userId) {
        return userAddressMapper.findDefaultUserAddressByUserId(userId);
    }

    @Override
    public boolean deleteAddressById(Long addressId, Long userId) {
        MallUserAddress userAddress = userAddressMapper.selectByPrimaryKey(addressId);
        if (!userAddress.getUserId().equals(userId)) {
            throw new YeeLeiMallException(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        return userAddressMapper.deleteByPrimaryKey(addressId) > 0;
    }

    @Override
    public List<MallUserAddressVO> getMyAddressList(Long userId) {
        List<MallUserAddress> userAddressList = userAddressMapper.getMyAddressList(userId);
        List<MallUserAddressVO> mallUserAddressVOList =
                CopyListUtil.copyListProperties(userAddressList, MallUserAddressVO::new);
        return mallUserAddressVOList;
    }
}
