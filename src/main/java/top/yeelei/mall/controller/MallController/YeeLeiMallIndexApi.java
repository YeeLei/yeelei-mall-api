package top.yeelei.mall.controller.MallController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.IndexConfigTypeEnum;
import top.yeelei.mall.controller.MallController.vo.MallIndexCarouselVO;
import top.yeelei.mall.controller.MallController.vo.MallIndexConfigGoodsVO;
import top.yeelei.mall.controller.MallController.vo.MallIndexInfoVO;
import top.yeelei.mall.service.AdminCarouselService;
import top.yeelei.mall.service.AdminIndexConfigService;

import java.util.List;


@RestController
@Api(tags = "商城首页接口")
@RequestMapping("/api")
public class YeeLeiMallIndexApi {
    @Autowired
    private AdminCarouselService adminCarouselService;
    @Autowired
    private AdminIndexConfigService adminIndexConfigService;

    @GetMapping("/index-infos")
    @ApiOperation(value = "获取首页数据", notes = "轮播图、新品、推荐等")
    public ApiRestResponse indexInfo() {
        MallIndexInfoVO indexInfoVO = new MallIndexInfoVO();
        List<MallIndexCarouselVO> carousels =
                adminCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<MallIndexConfigGoodsVO> hotGoodsList =
                adminIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsVO> newGoodsList =
                adminIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsVO> recommendGoodsList =
                adminIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoods(hotGoodsList);
        indexInfoVO.setNewGoods(newGoodsList);
        indexInfoVO.setRecommendGoods(recommendGoodsList);
        return ApiRestResponse.genSuccessResult(indexInfoVO);
    }
}
