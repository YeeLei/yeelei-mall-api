package top.yeelei.mall.controller.admin;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.IndexConfigTypeEnum;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.admin.param.AddIndexConfigParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateIndexConfigParam;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.IndexConfig;
import top.yeelei.mall.service.AdminIndexConfigService;

import javax.validation.Valid;

@RestController
@Api(tags = "后台管理系统首页配置模块接口")
@RequestMapping("/manage-api")
public class AdminIndexConfigApi {
    @Autowired
    private AdminIndexConfigService configService;

    @PostMapping("/indexConfigs")
    @ApiOperation(value = "新增首页配置项", notes = "新增首页配置项")
    public ApiRestResponse add(@RequestBody @Valid AddIndexConfigParam addIndexConfigParam,
                               @TokenToAdminUser AdminUserToken adminUser) {
        if (!configService.add(addIndexConfigParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("添加失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/indexConfigs")
    @ApiOperation(value = "修改首页配置项", notes = "修改首页配置项")
    public ApiRestResponse update(@RequestBody @Valid UpdateIndexConfigParam updateIndexConfigParam,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        if (!configService.update(updateIndexConfigParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("更新失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/indexConfigs/{configId}")
    @ApiOperation(value = "获取首页配置项详情", notes = "根据configId查询")
    public ApiRestResponse update(@PathVariable("configId") Long configId,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        IndexConfig indexConfig = configService.getIndexConfigById(configId);
        if (indexConfig == null) {
            return ApiRestResponse.genFailResult("获取详情失败!");
        }
        return ApiRestResponse.genSuccessResult(indexConfig);
    }

    @DeleteMapping("/indexConfigs/delete")
    @ApiOperation(value = "删除首页配置项", notes = "根据ids数组删除")
    public ApiRestResponse delete(@RequestBody @Valid BatchIdParam batchIdParam,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        if (!configService.deleteByBatchIds(batchIdParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("删除失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/indexConfigs/list")
    @ApiOperation(value = "获取首页配置项列表", notes = "获取首页配置项列表")
    public ApiRestResponse delete(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                  @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                  @RequestParam(required = false)
                                      @ApiParam(value = "1-搜索框热搜 2-搜索下拉框热搜 3-(首页)热销商品 4-(首页)新品上线 5-(首页)为你推荐")
                                              Integer configType,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 10) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)) {
            return ApiRestResponse.genFailResult("非法参数！");
        }
        PageInfo pageInfo = configService.getIndexConfigList(pageNum, pageSize, configType);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }
}
