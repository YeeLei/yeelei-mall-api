package top.yeelei.mall.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.admin.param.AdminLoginParam;
import top.yeelei.mall.controller.admin.param.UpdateAdminNameParam;
import top.yeelei.mall.controller.admin.param.UpdateAdminPasswordParam;
import top.yeelei.mall.model.pojo.AdminUser;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.service.AdminManageUserService;

import javax.validation.Valid;

@RestController
@Api(tags = "后台管理系统管理员模块接口")
@RequestMapping("/manage-api")
public class AdminManageUserApi {
    @Autowired
    private AdminManageUserService adminManageUserService;

    @PostMapping("/adminUser/login")
    @ApiOperation("登录接口")
    public ApiRestResponse login(@RequestBody @Valid AdminLoginParam adminLoginParam) {
        if (adminLoginParam == null || StringUtils.isEmpty(adminLoginParam.getUsername())
                || StringUtils.isEmpty(adminLoginParam.getPassword())) {
            return ApiRestResponse.genFailResult("用户名或密码不能为空");
        }
        String token = adminManageUserService.login(adminLoginParam.getUsername(), adminLoginParam.getPassword());

        //登录成功
        if (!StringUtils.isEmpty(token) && token.length() == Constants.TOKEN_LENGTH) {
            ApiRestResponse apiRestResponse = ApiRestResponse.genSuccessResult();
            apiRestResponse.setData(token);
            return apiRestResponse;
        }
        //登录失败
        return ApiRestResponse.genFailResult(token);
    }

    @GetMapping("/adminUser/profile")
    @ApiOperation("管理员信息接口")
    public ApiRestResponse profile(@TokenToAdminUser AdminUserToken adminUser) {
        AdminUser adminUserEntity = adminManageUserService.getUserDetailById(adminUser.getAdminUserId());
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            return ApiRestResponse.genSuccessResult(adminUserEntity);
        }
        return ApiRestResponse.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    @PutMapping("/adminUser/name")
    @ApiOperation("修改管理员名称接口")
    public ApiRestResponse updateName(@RequestBody UpdateAdminNameParam adminNameParam,
                                      @TokenToAdminUser AdminUserToken adminUser) {
        if (StringUtils.isEmpty(adminNameParam.getLoginUserName()) ||
                StringUtils.isEmpty(adminNameParam.getNickName())) {
            return ApiRestResponse.genFailResult(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        boolean flag = adminManageUserService.updateName(adminUser.getAdminUserId(),
                adminNameParam.getLoginUserName(), adminNameParam.getNickName());
        if (flag) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }

    @PutMapping("/adminUser/password")
    @ApiOperation("修改管理员密码接口")
    public ApiRestResponse updatePassword(@RequestBody UpdateAdminPasswordParam adminPasswordParam,
                                          @TokenToAdminUser AdminUserToken adminUser) {
        if (StringUtils.isEmpty(adminPasswordParam.getNewPassword()) ||
                StringUtils.isEmpty(adminPasswordParam.getOriginalPassword())) {
            return ApiRestResponse.genFailResult(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        boolean flag = adminManageUserService.updatePassword(adminUser.getAdminUserId(),
                adminPasswordParam.getNewPassword(),
                adminPasswordParam.getOriginalPassword());
        if (flag) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
    }

    @DeleteMapping("/adminUser/logout")
    @ApiOperation("管理员注销")
    public ApiRestResponse logout(@TokenToAdminUser AdminUserToken adminUser) {
        if (adminManageUserService.logout(adminUser.getAdminUserId())) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult("注销失败");
    }
}
