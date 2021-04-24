package top.yeelei.mall.controller.MallController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.MallController.param.MallUserLoginParam;
import top.yeelei.mall.controller.MallController.param.MallUserRegisterParam;
import top.yeelei.mall.controller.MallController.param.MallUserUpdateParam;
import top.yeelei.mall.controller.MallController.vo.MallUserVO;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.service.MallUserService;
import top.yeelei.mall.utils.NumberUtil;

import javax.validation.Valid;

/**
 * 用户个人Api
 */
@Api(tags = "商城用户操作相关接口")
@RestController
@RequestMapping("/api")
public class YeeLeiMallPersonalApi {

    @Autowired
    private MallUserService mallUserService;

    @PostMapping("/user/register")
    @ApiOperation(value = "用户注册", notes = "")
    public ApiRestResponse register(@RequestBody @Valid MallUserRegisterParam mallUserRegisterParam) {
        //判断输入的用户名是否为电话号码
        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())) {
            return ApiRestResponse.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String registerResult = mallUserService.register(mallUserRegisterParam.getLoginName(),
                mallUserRegisterParam.getPassword());

        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ApiRestResponse.genSuccessResult();
        }
        //注册失败
        return ApiRestResponse.genFailResult(registerResult);
    }


    @PostMapping("/user/login")
    @ApiOperation(value = "登录接口", notes = "返回token")
    public ApiRestResponse login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam) {
        //判断输入的用户名是否为电话号码
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ApiRestResponse.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String token = mallUserService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPassword());
        if (!StringUtils.isEmpty(token) && token.length() == Constants.TOKEN_LENGTH) {
            //token不为空并且token长度不超过32
            return ApiRestResponse.genSuccessResult(token);
        }
        //登录失败
        return ApiRestResponse.genFailResult(token);
    }


    @PostMapping("/user/logout")
    @ApiOperation(value = "登出接口", notes = "清除token")
    public ApiRestResponse logout(@TokenToMallUser MallUser loginUser) {
        boolean logout = mallUserService.logout(loginUser.getUserId());
        //登出成功
        if (logout) {
            return ApiRestResponse.genSuccessResult();
        }
        //登出失败
        return ApiRestResponse.genFailResult("logout error");
    }

    @GetMapping("/user/info")
    @ApiOperation(value = "获取用户信息", notes = "")
    public ApiRestResponse getUserInfo(@TokenToMallUser MallUser loginMallUser) {
        //已登录则直接返回
        MallUserVO mallUserVO = new MallUserVO();
        BeanUtils.copyProperties(loginMallUser, mallUserVO);
        return ApiRestResponse.genSuccessResult(mallUserVO);
    }

    @PutMapping("/user/info")
    @ApiOperation(value = "修改用户信息", notes = "")
    public ApiRestResponse updateUserInfo(@RequestBody @ApiParam(value = "用户信息",example = "")
                                                      MallUserUpdateParam mallUserUpdateParam,
                                          @TokenToMallUser MallUser loginMallUser) {
        Boolean flag = mallUserService.updateUserInfo(mallUserUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            return ApiRestResponse.genSuccessResult();
        } else {
            //返回失败
            return ApiRestResponse.genFailResult("修改失败");
        }
    }
}
