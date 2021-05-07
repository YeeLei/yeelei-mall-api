package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.controller.admin.param.AddIndexConfigParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateIndexConfigParam;
import top.yeelei.mall.controller.mall.vo.MallIndexConfigGoodsVO;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.IndexConfigMapper;
import top.yeelei.mall.model.dao.YeeLeiMallGoodsMapper;
import top.yeelei.mall.model.pojo.IndexConfig;
import top.yeelei.mall.model.pojo.YeeLeiMallGoods;
import top.yeelei.mall.service.AdminIndexConfigService;
import top.yeelei.mall.utils.CopyListUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminIndexConfigServiceImpl implements AdminIndexConfigService {
    @Autowired
    private IndexConfigMapper indexConfigMapper;
    @Autowired
    private YeeLeiMallGoodsMapper goodsMapper;

    @Override
    public boolean add(AddIndexConfigParam addIndexConfigParam, Long adminUserId) {
        IndexConfig indexConfigOld = indexConfigMapper.selectByNameAndTypeAndGoodsId(addIndexConfigParam.getConfigName(),
                addIndexConfigParam.getConfigType(), addIndexConfigParam.getGoodsId());
        if (indexConfigOld != null) {
            throw new YeeLeiMallException(ServiceResultEnum.INDEX_CONFIG_ITEM_IS_EXIST.getResult());
        }
        IndexConfig indexConfig = new IndexConfig();
        BeanUtils.copyProperties(addIndexConfigParam, indexConfig);
        long userId = adminUserId;
        indexConfig.setCreateUser((int) userId);
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(UpdateIndexConfigParam updateIndexConfigParam, Long adminUserId) {
        IndexConfig indexConfigOld = indexConfigMapper.selectByPrimaryKey(updateIndexConfigParam.getConfigId());
        if (indexConfigOld == null) {
            throw new YeeLeiMallException(ServiceResultEnum.INDEX_CONFIG_ITEM_NOT_EXIST.getResult());
        }
        IndexConfig indexConfig = new IndexConfig();
        BeanUtils.copyProperties(updateIndexConfigParam, indexConfig);
        indexConfig.setUpdateTime(new Date());
        long userId = adminUserId;
        indexConfig.setUpdateUser((int) userId);
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public IndexConfig getIndexConfigById(Long configId) {
        return indexConfigMapper.selectByPrimaryKey(configId);
    }

    @Override
    public boolean deleteByBatchIds(BatchIdParam batchIdParam, Long adminUserId) {
        return indexConfigMapper.updateIndexConfigByIds(batchIdParam.getIds(), adminUserId);
    }

    @Override
    public PageInfo getIndexConfigList(Integer pageNum, Integer pageSize, Integer configType) {
        PageHelper.startPage(pageNum, pageSize, "config_rank desc,create_time desc");
        List<IndexConfig> configList = indexConfigMapper.getIndexConfigList(configType);
        PageInfo<IndexConfig> pageInfo = new PageInfo<>(configList);
        return pageInfo;
    }

    @Override
    public List<MallIndexConfigGoodsVO> getConfigGoodsForIndex(int configType, int number) {
        List<MallIndexConfigGoodsVO> mallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.
                selectIndexConfigByConfigTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<YeeLeiMallGoods> goods = goodsMapper.selectByPrimaryKeys(goodIds);
            mallIndexConfigGoodsVOS = CopyListUtil.copyListProperties(goods, MallIndexConfigGoodsVO::new);
            for (MallIndexConfigGoodsVO mallIndexConfigGoodsVO : mallIndexConfigGoodsVOS) {
                String goodsName = mallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = mallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    mallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    mallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return mallIndexConfigGoodsVOS;
    }
}
