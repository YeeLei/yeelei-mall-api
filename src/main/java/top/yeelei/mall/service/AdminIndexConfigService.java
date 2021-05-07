package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.admin.param.AddIndexConfigParam;
import top.yeelei.mall.controller.admin.param.BatchIdParam;
import top.yeelei.mall.controller.admin.param.UpdateIndexConfigParam;
import top.yeelei.mall.controller.mall.vo.MallIndexConfigGoodsVO;
import top.yeelei.mall.model.pojo.IndexConfig;

import java.util.List;

public interface AdminIndexConfigService {
    /**
     * 添加首页配置项
     * @param addIndexConfigParam
     * @param adminUserId
     * @return
     */
    boolean add(AddIndexConfigParam addIndexConfigParam, Long adminUserId);

    /**
     * 更新首页配置项
     * @param updateIndexConfigParam
     * @param adminUserId
     * @return
     */
    boolean update(UpdateIndexConfigParam updateIndexConfigParam, Long adminUserId);

    /**
     * 根据configId获取首页配置项
     * @param configId
     * @return
     */
    IndexConfig getIndexConfigById(Long configId);

    /**
     * 批量删除首页配置项
     * @param batchIdParam
     * @param adminUserId
     * @return
     */
    boolean deleteByBatchIds(BatchIdParam batchIdParam, Long adminUserId);

    /**
     * 获取首页配置项列表（后台）
     * @param pageNum
     * @param pageSize
     * @param configType
     * @return
     */
    PageInfo getIndexConfigList(Integer pageNum, Integer pageSize, Integer configType);

    /**
     * 根据type和number获取首页商品展示(前台)
     * @param type
     * @param number
     * @return
     */
    List<MallIndexConfigGoodsVO> getConfigGoodsForIndex(int type, int number);
}
