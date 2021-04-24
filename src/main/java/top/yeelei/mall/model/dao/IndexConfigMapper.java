package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.IndexConfig;

import java.util.List;

@Repository
public interface IndexConfigMapper {
    int deleteByPrimaryKey(Long configId);

    int insert(IndexConfig record);

    int insertSelective(IndexConfig record);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig record);

    int updateByPrimaryKey(IndexConfig record);

    IndexConfig selectByNameAndTypeAndGoodsId(@Param("configName") String configName,
                                              @Param("configType") Byte configType,
                                              @Param("goodsId") Long goodsId);

    boolean updateIndexConfigByIds(@Param("ids") Long[] ids,
                                   @Param("adminUserId") Long adminUserId);

    List<IndexConfig> getIndexConfigList(@Param("configType") Integer configType);

    List<IndexConfig> selectIndexConfigByConfigTypeAndNum(@Param("configType") int configType,
                                                          @Param("number") int number);
}