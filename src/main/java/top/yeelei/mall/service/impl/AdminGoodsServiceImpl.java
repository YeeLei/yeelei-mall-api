package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.admin.param.AddGoodsParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateGoodsParam;
import top.yeelei.mall.controller.mall.vo.MallGoodsDetailVO;
import top.yeelei.mall.controller.mall.vo.MallSearchGoodsVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.GoodsCategoryMapper;
import top.yeelei.mall.model.dao.YeeLeiMallGoodsMapper;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.model.pojo.GoodsCategory;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;
import top.yeelei.mall.service.AdminGoodsService;
import top.yeelei.mall.utils.CopyListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminGoodsServiceImpl implements AdminGoodsService {
    @Autowired
    private YeeLeiMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper categoryMapper;

    @Override
    public boolean add(AddGoodsParam addGoodsParam, Long adminUserId) {
        YeeLeiMallGoods goods = new YeeLeiMallGoods();
        BeanUtils.copyProperties(addGoodsParam, goods);
        long userId = adminUserId;
        goods.setCreateUser((int) userId);
        if (goodsMapper.insertSelective(goods) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(UpdateGoodsParam updateGoodsParam, Long adminUserId) {
        YeeLeiMallGoods oldGoods = goodsMapper.selectByPrimaryKey(updateGoodsParam.getGoodsId());
        if (oldGoods == null) {
            throw new YeeLeiMallException(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        YeeLeiMallGoods goods = new YeeLeiMallGoods();
        BeanUtils.copyProperties(updateGoodsParam, goods);
        long userId = adminUserId;
        goods.setUpdateUser((int) userId);
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Map getGoodsInfo(Long goodsId) {
        YeeLeiMallGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw new YeeLeiMallException(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        Map goodsInfo = new HashMap(8);
        goodsInfo.put("goods", goods);
        GoodsCategory thirdCategory;
        GoodsCategory secondCategory;
        GoodsCategory firstCategory;
        thirdCategory = categoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        if (thirdCategory !=null) {
            goodsInfo.put("thirdCategory", thirdCategory);
            secondCategory = categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
            if (secondCategory != null) {
                goodsInfo.put("secondCategory", secondCategory);
                firstCategory = categoryMapper.selectByPrimaryKey(secondCategory.getParentId());
                if (firstCategory != null) {
                    goodsInfo.put("firstCategory", firstCategory);
                }
            }
        }
        return goodsInfo;
    }

    @Override
    public boolean batchUpdateSellStatus(Long sellStatus, BatchIdParam batchIdParam, AdminUserToken adminUser) {
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            throw new YeeLeiMallException(ServiceResultEnum.SELL_STATUS_ERROR.getResult());
        }
        if (goodsMapper.batchUpdateSellStatus(sellStatus, batchIdParam.getIds(), adminUser.getAdminUserId()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo listGoodsForAdmin(Integer pageNum, Integer pageSize, String goodsName, Integer goodsSellStatus) {
        PageHelper.startPage(pageNum, pageSize, "goods_id desc");
        List<YeeLeiMallGoods> goodsList = goodsMapper.listGoodsForAdmin(goodsName, goodsSellStatus);
        System.out.println(goodsList);
        PageInfo<YeeLeiMallGoods> pageInfo = new PageInfo<>(goodsList);
        return pageInfo;
    }

    @Override
    public MallGoodsDetailVO getMallGoodsById(Long goodsId) {
        if (goodsId < 1) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        YeeLeiMallGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw new YeeLeiMallException(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (goods.getGoodsSellStatus() != Constants.SELL_STATUS_UP) {
            //商品未上架
            throw new YeeLeiMallException(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        MallGoodsDetailVO mallGoodsDetailVO = new MallGoodsDetailVO();
        BeanUtils.copyProperties(goods, mallGoodsDetailVO);
        mallGoodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        return mallGoodsDetailVO;
    }

    @Override
    public PageInfo searchMallGoods(String keyword, Long goodsCategoryId, String orderBy, Integer pageNum) {
        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }
        int goodsSellStatus = Constants.SELL_STATUS_UP;
        int pageSize = Constants.GOODS_SEARCH_PAGE_LIMIT;

        PageHelper.startPage(pageNum,pageSize);

        List<YeeLeiMallGoods> goodsList = goodsMapper.selectMallGoodsListBySearch(keyword, goodsCategoryId,
                orderBy, goodsSellStatus);
        List<MallSearchGoodsVO> mallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            mallSearchGoodsVOS = CopyListUtil.copyListProperties(goodsList,MallSearchGoodsVO::new);
            for (MallSearchGoodsVO mallSearchGoodsVO : mallSearchGoodsVOS) {
                String goodsName = mallSearchGoodsVO.getGoodsName();
                String goodsIntro = mallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    mallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    mallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageInfo<MallSearchGoodsVO> pageInfo = new PageInfo<>(mallSearchGoodsVOS);
        return pageInfo;
    }
}
