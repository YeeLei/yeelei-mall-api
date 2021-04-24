package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.common.YeeLeiMallCategoryLevelEnum;
import top.yeelei.mall.controller.AdminController.param.AddGoodsCategoryParam;
import top.yeelei.mall.controller.AdminController.param.BatchIdParam;
import top.yeelei.mall.controller.AdminController.param.UpdateGoodsCategoryParam;
import top.yeelei.mall.controller.MallController.vo.MallIndexCategoryVO;
import top.yeelei.mall.controller.MallController.vo.SecondLevelCategoryVO;
import top.yeelei.mall.controller.MallController.vo.ThirdLevelCategoryVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.AdminUserMapper;
import top.yeelei.mall.model.dao.GoodsCategoryMapper;
import top.yeelei.mall.model.pojo.AdminUser;
import top.yeelei.mall.model.pojo.GoodsCategory;
import top.yeelei.mall.service.AdminGoodsCategoryService;
import top.yeelei.mall.utils.CopyListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AdminGoodsCategoryServiceImpl implements AdminGoodsCategoryService {
    @Autowired
    private GoodsCategoryMapper categoryMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public boolean addGoodsCategory(AddGoodsCategoryParam addGoodsCategoryParam, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            GoodsCategory goodsCategoryOld = categoryMapper.selectByLevelAndName(addGoodsCategoryParam.getCategoryLevel(),
                    addGoodsCategoryParam.getCategoryName());
            if (goodsCategoryOld != null) {
                throw new YeeLeiMallException(ServiceResultEnum.SAME_CATEGORY_EXIST.getResult());
            } else {
                //新增商品分类
                GoodsCategory goodsCategory = new GoodsCategory();
                goodsCategory.setCategoryLevel(addGoodsCategoryParam.getCategoryLevel());
                goodsCategory.setCategoryName(addGoodsCategoryParam.getCategoryName());
                goodsCategory.setCategoryRank(addGoodsCategoryParam.getCategoryRank());
                goodsCategory.setParentId(addGoodsCategoryParam.getParentId());
                long userId = adminUser.getAdminUserId();
                goodsCategory.setCreateUser((int) userId);
                //插入
                if (categoryMapper.insertSelective(goodsCategory) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateGoodsCategory(UpdateGoodsCategoryParam updateGoodsCategoryParam, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            GoodsCategory goodsCategory = categoryMapper.selectByPrimaryKey(updateGoodsCategoryParam.getCategoryId());
            if (goodsCategory == null) {
                throw new YeeLeiMallException(ServiceResultEnum.CATEGORY_ID_NOT_EXIST.getResult());
            }
            GoodsCategory goodsCategoryOld = categoryMapper.selectByLevelAndName(updateGoodsCategoryParam.getCategoryLevel(),
                    updateGoodsCategoryParam.getCategoryName());
            if (goodsCategoryOld != null) {
                throw new YeeLeiMallException(ServiceResultEnum.SAME_CATEGORY_EXIST.getResult());
            } else {
                //更新商品分类
                GoodsCategory newGoodsCategory = new GoodsCategory();
                newGoodsCategory.setCategoryId(updateGoodsCategoryParam.getCategoryId());
                newGoodsCategory.setCategoryLevel(updateGoodsCategoryParam.getCategoryLevel());
                newGoodsCategory.setCategoryName(updateGoodsCategoryParam.getCategoryName());
                newGoodsCategory.setCategoryRank(updateGoodsCategoryParam.getCategoryRank());
                newGoodsCategory.setParentId(updateGoodsCategoryParam.getParentId());
                long userId = adminUser.getAdminUserId();
                newGoodsCategory.setUpdateUser((int) userId);
                //插入
                if (categoryMapper.updateByPrimaryKeySelective(newGoodsCategory) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public GoodsCategory getGoodsCategoryInfo(Long categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new YeeLeiMallException(ServiceResultEnum.CATEGORY_ID_NOT_NULL.getResult());
        }
        GoodsCategory goodsCategory = categoryMapper.selectByPrimaryKey(categoryId);
        return goodsCategory;
    }

    @Override
    public boolean deleteGoodsCategory(BatchIdParam batchIdParam, Long adminUserId) {
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.CATEGORY_ID_NOT_NULL.getResult());
        }
        if (categoryMapper.deleteByIds(batchIdParam.getIds(), adminUserId)) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize, Integer categoryLevel, Long parentId) {
        PageHelper.startPage(pageNum, pageSize, "category_rank desc");
        List<GoodsCategory> goodsCategoryList = categoryMapper.listCategoryForAdmin(categoryLevel, parentId);
        PageInfo<GoodsCategory> pageInfo = new PageInfo<>(goodsCategoryList);
        return pageInfo;
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long categoryId) {
        return categoryMapper.selectByPrimaryKey(categoryId);
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int level) {
        return categoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, level, 0);//0代表查询所有
    }

    @Override
    public List<MallIndexCategoryVO> getGoodsCategoryForIndex() {
        List<MallIndexCategoryVO> mallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L),
                YeeLeiMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //如果一级分类不为空，则查询固定数量的二级分类数据
            //先拿到所有一级分类的id
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream()
                    .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //查询所有二级分类数据
            List<GoodsCategory> secondLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                    firstLevelCategoryIds, YeeLeiMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //如果二级分类不为空，则查询所有的三级分类
                //先拿到所有二级分类的id
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream()
                        .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                        secondLevelCategoryIds, YeeLeiMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //如果三级分类不为空，则根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream()
                            .collect(Collectors.groupingBy(GoodsCategory::getParentId));

                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtils.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.
                                    get(secondLevelCategory.getCategoryId());
                            List<ThirdLevelCategoryVO> thirdLevelCategoryVOS = CopyListUtil.
                                    copyListProperties(tempGoodsCategories, ThirdLevelCategoryVO::new);
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(thirdLevelCategoryVOS);
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 secondLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap =
                                secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstLevelCategory : firstLevelCategories) {
                            MallIndexCategoryVO mallIndexCategoryVO = new MallIndexCategoryVO();
                            BeanUtils.copyProperties(firstLevelCategory, mallIndexCategoryVO);
                            //如果该一级分类下有数据则放入 MallIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstLevelCategory.getCategoryId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.
                                        get(firstLevelCategory.getCategoryId());
                                mallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                mallIndexCategoryVOS.add(mallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return mallIndexCategoryVOS;
        } else {
            return null;
        }
    }
}
