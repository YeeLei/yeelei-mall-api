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
import top.yeelei.mall.controller.admin.param.AddGoodsCategoryParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateGoodsCategoryParam;
import top.yeelei.mall.controller.mall.vo.MallIndexCategoryVO;
import top.yeelei.mall.controller.mall.vo.SecondLevelCategoryVO;
import top.yeelei.mall.controller.mall.vo.ThirdLevelCategoryVO;
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
                //??????????????????
                GoodsCategory goodsCategory = new GoodsCategory();
                goodsCategory.setCategoryLevel(addGoodsCategoryParam.getCategoryLevel());
                goodsCategory.setCategoryName(addGoodsCategoryParam.getCategoryName());
                goodsCategory.setCategoryRank(addGoodsCategoryParam.getCategoryRank());
                goodsCategory.setParentId(addGoodsCategoryParam.getParentId());
                long userId = adminUser.getAdminUserId();
                goodsCategory.setCreateUser((int) userId);
                //??????
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
                //??????????????????
                GoodsCategory newGoodsCategory = new GoodsCategory();
                newGoodsCategory.setCategoryId(updateGoodsCategoryParam.getCategoryId());
                newGoodsCategory.setCategoryLevel(updateGoodsCategoryParam.getCategoryLevel());
                newGoodsCategory.setCategoryName(updateGoodsCategoryParam.getCategoryName());
                newGoodsCategory.setCategoryRank(updateGoodsCategoryParam.getCategoryRank());
                long userId = adminUser.getAdminUserId();
                newGoodsCategory.setUpdateUser((int) userId);
                //??????
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
        return categoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, level, 0);//0??????????????????
    }

    @Override
    public List<MallIndexCategoryVO> getGoodsCategoryForIndex() {
        List<MallIndexCategoryVO> mallIndexCategoryVOS = new ArrayList<>();
        //??????????????????????????????????????????
        List<GoodsCategory> firstLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L),
                YeeLeiMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //????????????????????????????????????????????????????????????????????????
            //??????????????????????????????id
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream()
                    .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //??????????????????????????????
            List<GoodsCategory> secondLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                    firstLevelCategoryIds, YeeLeiMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //????????????????????????????????????????????????????????????
                //??????????????????????????????id
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream()
                        .map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //???????????????????????????
                List<GoodsCategory> thirdLevelCategories = categoryMapper.selectByLevelAndParentIdsAndNumber(
                        secondLevelCategoryIds, YeeLeiMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //??????????????????????????????????????? parentId ??? thirdLevelCategories ??????
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream()
                            .collect(Collectors.groupingBy(GoodsCategory::getParentId));

                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtils.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //?????????????????????????????????????????? secondLevelCategoryVOS ?????????
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //?????????????????????id??????thirdLevelCategoryMap????????????????????????list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.
                                    get(secondLevelCategory.getCategoryId());
                            List<ThirdLevelCategoryVO> thirdLevelCategoryVOS = CopyListUtil.
                                    copyListProperties(tempGoodsCategories, ThirdLevelCategoryVO::new);
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(thirdLevelCategoryVOS);
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //??????????????????
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //?????? parentId ??? secondLevelCategories ??????
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap =
                                secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstLevelCategory : firstLevelCategories) {
                            MallIndexCategoryVO mallIndexCategoryVO = new MallIndexCategoryVO();
                            BeanUtils.copyProperties(firstLevelCategory, mallIndexCategoryVO);
                            //?????????????????????????????????????????? MallIndexCategoryVOS ?????????
                            if (secondLevelCategoryVOMap.containsKey(firstLevelCategory.getCategoryId())) {
                                //?????????????????????id??????secondLevelCategoryVOMap???????????????????????????list
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
