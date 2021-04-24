package top.yeelei.mall.controller.AdminController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.AdminController.param.BatchIdParam;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.service.MallUserService;

@RestController
@Api(tags = "后台管理系统注册用户模块接口")
@RequestMapping("/manage-api")
public class AdminRegisterUserApi {
    @Autowired
    private MallUserService mallUserService;

    @GetMapping("/users")
    @ApiOperation(value = "商城注册用户列表", notes = "商城注册用户列表")
    public ApiRestResponse list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                @RequestParam(required = false) @ApiParam(value = "用户状态") Integer lockStatus,
                                @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 10) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        PageInfo pageInfo = mallUserService.listForMallUser(pageNum, pageSize, lockStatus);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }

    @PutMapping("/users/{lockStatus}")
    @ApiOperation(value = "修改用户状态", notes = "批量修改，用户禁用与解除禁用(0-未锁定 1-已锁定)")
    public ApiRestResponse lockUser(@RequestBody BatchIdParam batchIdParam, @PathVariable Integer lockStatus, @TokenToAdminUser AdminUserToken adminUser) {
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus != 1) {
            return ApiRestResponse.genFailResult("操作非法！");
        }
        if (mallUserService.lockUsers(batchIdParam.getIds(), lockStatus)) {
            return ApiRestResponse.genSuccessResult();
        } else {
            return ApiRestResponse.genFailResult("禁用失败");
        }
    }
}
