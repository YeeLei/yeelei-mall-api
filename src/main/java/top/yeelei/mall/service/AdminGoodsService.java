package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.AdminController.param.AddGoodsCategoryParam;
import top.yeelei.mall.controller.AdminController.param.AddGoodsParam;
import top.yeelei.mall.controller.AdminController.param.BatchIdParam;
import top.yeelei.mall.controller.AdminController.param.UpdateGoodsParam;
import top.yeelei.mall.controller.MallController.vo.MallGoodsDetailVO;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;

public interface AdminGoodsService {
    /**
     * 添加商品
     * @param addGoodsParam
     * @param adminUserId
     * @return
     */
    boolean add(AddGoodsParam addGoodsParam, Long adminUserId);

    /**
     * 更新商品
     * @param updateGoodsParam
     * @param adminUserId
     * @return
     */
    boolean update(UpdateGoodsParam updateGoodsParam, Long adminUserId);

    /**
     * 根据goodsId获取商品详情
     * @param goodsId
     * @return
     */
    YeeLeiMallGoods getGoodsInfo(Long goodsId);

    /**
     * 批量上下架商品
     * @param sellStatus
     * @param batchIdParam
     * @param adminUser
     * @return
     */
    boolean batchUpdateSellStatus(Long sellStatus, BatchIdParam batchIdParam, AdminUserToken adminUser);

    /**
     * 后台商品列表
     * @param pageNum
     * @param pageSize
     * @param goodsName
     * @param goodsSellStatus
     * @return
     */
    PageInfo listGoodsForAdmin(Integer pageNum, Integer pageSize, String goodsName, Integer goodsSellStatus);

    /**
     * 前台商品详情
     * @param goodsId
     * @return
     */
    MallGoodsDetailVO getMallGoodsById(Long goodsId);

    /**
     * 前台商品搜索
     * @param keyword
     * @param goodsCategoryId
     * @param orderBy
     * @param pageNum
     * @return
     */
    PageInfo searchMallGoods(String keyword, Long goodsCategoryId, String orderBy, Integer pageNum);
}
