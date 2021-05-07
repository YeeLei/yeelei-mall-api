package top.yeelei.mall.controller.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.mall.param.AddMallUserAddressParam;
import top.yeelei.mall.controller.mall.param.UpdateMallUserAddressParam;
import top.yeelei.mall.controller.mall.vo.MallUserAddressVO;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.model.pojo.MallUserAddress;
import top.yeelei.mall.service.MallUserAddressService;

import java.util.List;

@RestController
@Api(tags = "商城个人地址相关接口")
@RequestMapping("/api")
public class YeeLeiMallUserAddressApi {
    @Autowired
    private MallUserAddressService userAddressService;

    @PostMapping("/address")
    @ApiOperation(value = "添加地址", notes = "")
    public ApiRestResponse saveUserAddress(@RequestBody AddMallUserAddressParam addMallUserAddressParam,
                                           @TokenToMallUser MallUser loginMallUser) {
        if (!userAddressService.addUserAddress(addMallUserAddressParam, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("添加收货地址失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation(value = "获取地址详情", notes = "参数为addressId")
    public ApiRestResponse updateMallUserAddress(@PathVariable("addressId") Long addressId,
                                                 @TokenToMallUser MallUser loginMallUser) {
        MallUserAddressVO mallUserAddressVO =
                userAddressService.getUserAddressById(addressId);
        return ApiRestResponse.genSuccessResult(mallUserAddressVO);
    }

    @PutMapping("/address")
    @ApiOperation(value = "修改地址", notes = "")
    public ApiRestResponse updateMallUserAddress(@RequestBody UpdateMallUserAddressParam updateMallUserAddressParam,
                                                 @TokenToMallUser MallUser loginMallUser) {
        if (!userAddressService.updateMallUserAddress(updateMallUserAddressParam, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("更新收货地址失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/address/default")
    @ApiOperation(value = "获取默认收货地址", notes = "无传参")
    public ApiRestResponse getDefaultMallUserAddress(@TokenToMallUser MallUser loginMallUser) {
        MallUserAddress mallUserAddress =
                userAddressService.getDefaultMallUserAddress(loginMallUser.getUserId());
        if (mallUserAddress == null) {
            return ApiRestResponse.genFailResult("没有默认的收货地址!");
        }
        return ApiRestResponse.genSuccessResult(mallUserAddress);
    }

    @DeleteMapping("/address/{addressId}")
    @ApiOperation(value = "删除收货地址", notes = "传参为地址id")
    public ApiRestResponse deleteAddress(@PathVariable("addressId") Long addressId,
                                         @TokenToMallUser MallUser loginMallUser) {
        if (!userAddressService.deleteAddressById(addressId, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("删除地址失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/address")
    @ApiOperation(value = "我的收货地址列表", notes = "")
    public ApiRestResponse addressList(@TokenToMallUser MallUser loginMallUser) {
        List<MallUserAddressVO> mallUserAddressVOList =
                userAddressService.getMyAddressList(loginMallUser.getUserId());
        return ApiRestResponse.genSuccessResult(mallUserAddressVOList);
    }
}
