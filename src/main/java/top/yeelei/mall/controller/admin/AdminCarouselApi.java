package top.yeelei.mall.controller.admin;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.controller.admin.param.AddCarouselParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateCarouselParam;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.Carousel;
import top.yeelei.mall.service.AdminCarouselService;

import javax.validation.Valid;

@Api(tags = "后台管理系统轮播图模块接口")
@RestController
@RequestMapping("/manage-api")
public class AdminCarouselApi {
    @Autowired
    private AdminCarouselService adminCarouselService;

    @PostMapping("/carousels")
    @ApiOperation(value = "新增轮播图", notes = "新增轮播图")
    public ApiRestResponse add(@RequestBody @Valid AddCarouselParam addCarouselParam, @TokenToAdminUser AdminUserToken adminUser) {
        boolean flag = adminCarouselService.addCarousel(addCarouselParam, adminUser.getAdminUserId());
        if (flag) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult("添加失败!");
    }

    @GetMapping("/carousels/{carouselId}")
    @ApiOperation(value = "获取轮播图信息", notes = "获取轮播图信息")
    public ApiRestResponse get(@PathVariable("carouselId") Integer carouselId, @TokenToAdminUser AdminUserToken adminUser) {
        Carousel carousel = adminCarouselService.getCarouselById(carouselId, adminUser.getAdminUserId());
        if (carousel == null) {
            return ApiRestResponse.genFailResult("获取轮播图数据失败!");
        }
        return ApiRestResponse.genSuccessResult(carousel);
    }

    @PutMapping("/carousels")
    @ApiOperation(value = "修改轮播图", notes = "修改轮播图")
    public ApiRestResponse edit(@RequestBody @Valid UpdateCarouselParam updateCarouselParam,
                                @TokenToAdminUser AdminUserToken adminUser) {
        if (adminCarouselService.updateCarousel(updateCarouselParam, adminUser.getAdminUserId())) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult("更新失败!");
    }

    @DeleteMapping("/carousels")
    @ApiOperation(value = "批量删除轮播图", notes = "批量删除轮播图")
    public ApiRestResponse delete(@RequestBody @Valid BatchIdParam batchIdParam,
                                  @TokenToAdminUser AdminUserToken adminUser) {
        if (adminCarouselService.deleteCarouselByIds(batchIdParam.getIds(), adminUser.getAdminUserId())) {
            return ApiRestResponse.genSuccessResult();
        }
        return ApiRestResponse.genFailResult("删除失败!");
    }

    @GetMapping("/carousels")
    @ApiOperation(value = "获取轮播图列表", notes = "获取轮播图列表")
    public ApiRestResponse list(Integer pageNum, Integer pageSize,
                                @TokenToAdminUser AdminUserToken adminUser) {
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 10) {
            return ApiRestResponse.genFailResult("参数异常！");
        }
        PageInfo pageInfo = adminCarouselService.listCarouselForAdmin(pageNum, pageSize);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }
}
