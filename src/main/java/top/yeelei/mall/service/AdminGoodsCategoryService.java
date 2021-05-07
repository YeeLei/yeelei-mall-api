package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.admin.param.AddGoodsCategoryParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateGoodsCategoryParam;
import top.yeelei.mall.controller.mall.vo.MallIndexCategoryVO;
import top.yeelei.mall.model.pojo.GoodsCategory;

import java.util.List;

public interface AdminGoodsCategoryService {

    /**
     * 添加商品分类
     * @param addGoodsCategoryParam
     * @param adminUserId
     * @return
     */
    boolean addGoodsCategory(AddGoodsCategoryParam addGoodsCategoryParam, Long adminUserId);

    /**
     * 更新商品分类
     * @param updateGoodsCategoryParam
     * @param adminUserId
     * @return
     */
    boolean updateGoodsCategory(UpdateGoodsCategoryParam updateGoodsCategoryParam, Long adminUserId);

    /**
     * 获取商品分类详情
     * @param categoryId
     * @return
     */
    GoodsCategory getGoodsCategoryInfo(Long categoryId);

    /**
     * 批量删除商品分类
     * @param batchIdParam
     * @param adminUserId
     * @return
     */
    boolean deleteGoodsCategory(BatchIdParam batchIdParam, Long adminUserId);

    /**
     * 根据categoryId获取商品分类信息
     * @param categoryId
     * @return
     */
    GoodsCategory getGoodsCategoryById(Long categoryId);

    /**
     * 后台分页
     *
     * @param pageNum
     * @param pageSize
     * @param categoryLevel
     * @param parentId
     * @return
     */
    PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize, Integer categoryLevel, Long parentId);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param level
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int level);

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    List<MallIndexCategoryVO> getGoodsCategoryForIndex();
}
