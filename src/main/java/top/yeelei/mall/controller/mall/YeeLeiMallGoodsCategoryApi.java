package top.yeelei.mall.controller.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.mall.vo.MallIndexCategoryVO;
import top.yeelei.mall.service.AdminGoodsCategoryService;

import java.util.List;

@RestController
@Api(tags = "商城分类页面接口")
@RequestMapping("/api")
public class YeeLeiMallGoodsCategoryApi {
    @Autowired
    private AdminGoodsCategoryService goodsCategoryService;

    @GetMapping("/categories")
    @ApiOperation(value = "获取分类数据", notes = "分类页面使用")
    public ApiRestResponse getCategories() {
        List<MallIndexCategoryVO> categories = goodsCategoryService.getGoodsCategoryForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            ApiRestResponse.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ApiRestResponse.genSuccessResult(categories);
    }
}
