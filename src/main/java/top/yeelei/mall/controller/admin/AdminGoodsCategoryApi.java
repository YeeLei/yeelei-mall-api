package top.yeelei.mall.controller.admin;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.YeeLeiMallCategoryLevelEnum;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.admin.param.AddGoodsCategoryParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateGoodsCategoryParam;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.GoodsCategory;
import top.yeelei.mall.service.AdminGoodsCategoryService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台管理系统商品分类模块接口")
@RestController
@RequestMapping("/manage-api")
public class AdminGoodsCategoryApi {
    @Autowired
    private AdminGoodsCategoryService categoryService;

    @PostMapping("/goodsCategory")
    @ApiOperation(value = "新增分类", notes = "新增分类")
    public ApiRestResponse addGoodsCategory(@RequestBody @Valid AddGoodsCategoryParam addGoodsCategoryParam,
                                            @TokenToAdminUser AdminUserToken adminUser) {
        boolean flag = categoryService.addGoodsCategory(addGoodsCategoryParam, adminUser.getAdminUserId());
        if (!flag) {
            return ApiRestResponse.genFailResult("添加商品分类失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/goodsCategory")
    @ApiOperation(value = "修改分类", notes = "修改分类")
    public ApiRestResponse updateGoodsCategory(@RequestBody @Valid UpdateGoodsCategoryParam updateGoodsCategoryParam,
                                               @TokenToAdminUser AdminUserToken adminUser) {
        if (!categoryService.updateGoodsCategory(updateGoodsCategoryParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("更新商品分类失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/goodsCategory/{categoryId}")
    @ApiOperation(value = "获取分类信息", notes = "获取分类信息")
    public ApiRestResponse getGoodsCategoryInfo(@PathVariable Long categoryId, @TokenToAdminUser AdminUserToken adminUser) {
        GoodsCategory goodsCategory = categoryService.getGoodsCategoryInfo(categoryId);
        if (goodsCategory == null) {
            return ApiRestResponse.genFailResult("获取分类失败!");
        }
        return ApiRestResponse.genSuccessResult(goodsCategory);
    }

    @DeleteMapping("/goodsCategory")
    @ApiOperation(value = "批量删除分类信息", notes = "批量删除分类信息")
    public ApiRestResponse deleteGoodsCategory(@RequestBody BatchIdParam batchIdParam,
                                               @TokenToAdminUser AdminUserToken adminUser) {
        if (!categoryService.deleteGoodsCategory(batchIdParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genFailResult("删除商品分类失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/goodsCategory")
    @ApiOperation(value = "商品分类列表", notes = "根据级别和上级分类id查询")
    public ApiRestResponse listCategoryForAdmin(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                                @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                                                @RequestParam(required = false) @ApiParam(value = "分类级别") Integer categoryLevel,
                                                @RequestParam(required = false) @ApiParam(value = "上级分类的id") Long parentId,
                                                @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null
                || pageSize < 10 || categoryLevel == null || categoryLevel < 0
                || categoryLevel > 3 || parentId == null || parentId < 0) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum, pageSize, categoryLevel, parentId);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }

    @GetMapping("/categoriesSelect")
    @ApiOperation(value = "商品分类列表", notes = "用于三级分类联动效果制作")
    public ApiRestResponse listCategoryForSelect(@RequestParam Long categoryId,
                                                 @TokenToAdminUser AdminUserToken adminUser) {
        if (categoryId == null || categoryId < 0) {
            return ApiRestResponse.genFailResult("缺少参数！");
        }
        GoodsCategory category = categoryService.getGoodsCategoryById(categoryId);
        //既不是一级分类也不是二级分类则为不返回数据
        if (category == null || category.getCategoryLevel() == YeeLeiMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ApiRestResponse.genFailResult("缺少参数！");
        }
        Map categoryResult = new HashMap(4);
        if (category.getCategoryLevel() == YeeLeiMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(categoryId),
                    YeeLeiMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            //如果二级分类列表不为空
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(
                        Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),
                        YeeLeiMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == YeeLeiMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(categoryId), YeeLeiMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ApiRestResponse.genSuccessResult(categoryResult);
    }
}
