package top.yeelei.mall.controller.mall;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.config.annotation.TokenToMallUser;
import top.yeelei.mall.controller.mall.param.AddCartItemParam;
import top.yeelei.mall.controller.mall.param.UpdateCartItemParam;
import top.yeelei.mall.controller.mall.vo.MallShoppingCartItemVO;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.service.MallShoppingCartService;

import java.util.Arrays;
import java.util.List;

@RestController
@Api(tags = "商城购物车相关接口")
@RequestMapping("/api")
public class YeeLeiMallShopCartApi {

    @Autowired
    private MallShoppingCartService shoppingCartService;

    @PostMapping("/shop-cart")
    @ApiOperation(value = "添加商品到购物车接口", notes = "传参为商品id、数量")
    public ApiRestResponse addMallShoppingCartItem(@RequestBody AddCartItemParam addCartItemParam,
                                                   @TokenToMallUser MallUser loginMallUser) {
        if (!shoppingCartService.addMallShoppingCartItem(addCartItemParam, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("添加商品到购物车失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @PutMapping("/shop-cart")
    @ApiOperation(value = "修改购物项数据", notes = "传参为购物项id、数量")
    public ApiRestResponse updateNewBeeMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                            @TokenToMallUser MallUser loginMallUser) {
        if (!shoppingCartService.updateMallCartItem(updateCartItemParam, loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("更新购物车失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @DeleteMapping("/shop-cart/{shoppingCartItemId}")
    @ApiOperation(value = "删除购物项", notes = "传参为购物项id")
    public ApiRestResponse updateNewBeeMallShoppingCartItem(@PathVariable("shoppingCartItemId") Long shoppingCartItemId,
                                                   @TokenToMallUser MallUser loginMallUser) {
        if(!shoppingCartService.deleteByIdAndUserId(shoppingCartItemId,loginMallUser.getUserId())) {
            return ApiRestResponse.genFailResult("删除购物项失败!");
        }
        return ApiRestResponse.genSuccessResult();
    }

    @GetMapping("/shop-cart/settle")
    @ApiOperation(value = "根据购物项id数组查询购物项明细", notes = "确认订单页面使用")
    public ApiRestResponse toSettle(Long[] cartItemIds, @TokenToMallUser MallUser loginMallUser) {
        List<MallShoppingCartItemVO> itemsForSettle =
                shoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds),loginMallUser.getUserId());
        return ApiRestResponse.genSuccessResult(itemsForSettle);
    }

    @GetMapping("/shop-cart/page")
    @ApiOperation(value = "购物车列表(每页默认5条)", notes = "传参为页码")
    public ApiRestResponse cartItemPageList(Integer pageNum, @TokenToMallUser MallUser loginMallUser) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        PageInfo pageInfo = shoppingCartService.getMyShoppingCartItems(pageNum,loginMallUser.getUserId());
        if (pageInfo == null) {
            return ApiRestResponse.genFailResult("获取购物车列表失败!");
        }
        return ApiRestResponse.genSuccessResult(pageInfo);
    }

    @GetMapping("/shop-cart")
    @ApiOperation(value = "购物车列表(网页移动端不分页)", notes = "")
    public ApiRestResponse cartItemList(@TokenToMallUser MallUser loginMallUser) {
        List<MallShoppingCartItemVO> mallShoppingCartItemVOList =
                shoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId());
        return ApiRestResponse.genSuccessResult(mallShoppingCartItemVOList);
    }

}
