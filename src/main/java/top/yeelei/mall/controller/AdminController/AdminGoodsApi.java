package top.yeelei.mall.controller.AdminController;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.AdminController.param.AddGoodsParam;
import top.yeelei.mall.controller.AdminController.param.BatchIdParam;
import top.yeelei.mall.controller.AdminController.param.UpdateGoodsParam;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;
import top.yeelei.mall.service.AdminGoodsService;

import javax.validation.Valid;

@Api(tags = "后台管理系统商品模块接口")
@RestController
@RequestMapping("/manage-api")
public class AdminGoodsApi {
    @Autowired
    private AdminGoodsService adminGoodsService;

    @PostMapping("/goods")
    @ApiOperation(value = "新增商品信息", notes = "新增商品信息")
    public ApiRestResponse addGoods(@RequestBody @Valid AddGoodsParam addGoodsParam,
                                    @TokenToAdminUser AdminUserToken adminUser) {
        if(!adminGoodsService.add(addGoodsParam,adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("添加失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/goods")
    @ApiOperation(value = "修改商品信息", notes = "修改商品信息")
    public ApiRestResponse updateGoods(@RequestBody @Valid UpdateGoodsParam updateGoodsParam,
                                    @TokenToAdminUser AdminUserToken adminUser) {
        if(!adminGoodsService.update(updateGoodsParam,adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("更新失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/goods/{goodsId}")
    @ApiOperation(value = "获取商品信息", notes = "根据goodsId")
    public ApiRestResponse updateGoods(@PathVariable("goodsId") Long goodsId,
                                       @TokenToAdminUser AdminUserToken adminUser) {
        YeeLeiMallGoods goods = adminGoodsService.getGoodsInfo(goodsId);
        return ApiRestResponse.genSuccessResult(goods);
    }

    @PutMapping("/goods/status/{sellStatus}")
    @ApiOperation(value = "批量修改销售状态", notes = "批量修改销售状态")
    public ApiRestResponse delete(@PathVariable("sellStatus") Long sellStatus,
                                       @RequestBody @Valid BatchIdParam batchIdParam,
                                       @TokenToAdminUser AdminUserToken adminUser) {
        if (!adminGoodsService.batchUpdateSellStatus(sellStatus,batchIdParam,adminUser)) {
            return ApiRestResponse.genSuccessResult("修改销售状态失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/goods/list")
    @ApiOperation(value = "获取商品列表", notes = "可根据名称和上架状态筛选")
    public ApiRestResponse delete(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                  @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                  @RequestParam(required = false) @ApiParam(value = "商品名称") String goodsName,
                                  @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架")
                                              Integer goodsSellStatus,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 10) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        PageInfo pageInfo = adminGoodsService.listGoodsForAdmin(pageNum,pageSize,goodsName,goodsSellStatus);
        if (pageInfo == null) {
            return ApiRestResponse.genSuccessResult("获取商品列表失败!");
        }
        return ApiRestResponse.genSuccessResult(pageInfo);
    }
}
