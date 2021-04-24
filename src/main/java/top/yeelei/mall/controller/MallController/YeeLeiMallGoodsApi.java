package top.yeelei.mall.controller.MallController;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.MallController.vo.MallGoodsDetailVO;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.service.AdminGoodsService;

@RestController
@Api(tags = "商城商品相关接口")
@RequestMapping("/api")
public class YeeLeiMallGoodsApi {
    @Autowired
    private AdminGoodsService goodsService;

    @GetMapping("/goods/detail/{goodsId}")
    @ApiOperation(value = "商品详情接口", notes = "传参为商品id")
    public ApiRestResponse goodsDetail(@ApiParam(value = "商品id") @PathVariable("goodsId") Long goodsId,
                                       @TokenToMallUser MallUser loginMallUser) {
        MallGoodsDetailVO goods = goodsService.getMallGoodsById(goodsId);
        return ApiRestResponse.genSuccessResult(goods);
    }

    @GetMapping("/search")
    @ApiOperation(value = "商品搜索接口", notes = "根据关键字和分类id进行搜索")
    public ApiRestResponse search(@RequestParam(required = false) @ApiParam(value = "搜索关键字") String keyword,
                                  @RequestParam(required = false) @ApiParam(value = "商品分类id") Long goodsCategoryId,
                                  @RequestParam(required = false) @ApiParam(value = "orderBy") String orderBy,
                                  @RequestParam(required = false) @ApiParam(value = "页码") Integer pageNum,
                                  @TokenToMallUser MallUser loginMallUser) {
        if (goodsCategoryId == null || StringUtils.isEmpty(keyword)) {
            return ApiRestResponse.genFailResult("非法的搜索参数");
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        PageInfo pageInfo = goodsService.searchMallGoods(keyword,goodsCategoryId,orderBy, pageNum);
        return ApiRestResponse.genSuccessResult(pageInfo);
    }
}
