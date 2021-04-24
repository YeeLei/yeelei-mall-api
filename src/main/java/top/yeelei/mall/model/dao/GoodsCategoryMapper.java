package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.GoodsCategory;

import java.util.List;

@Repository
public interface GoodsCategoryMapper {
    int deleteByPrimaryKey(Long categoryId);

    int insert(GoodsCategory record);

    int insertSelective(GoodsCategory record);

    GoodsCategory selectByPrimaryKey(Long categoryId);

    int updateByPrimaryKeySelective(GoodsCategory record);

    int updateByPrimaryKey(GoodsCategory record);

    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel,
                                       @Param("categoryName") String categoryName);

    boolean deleteByIds(@Param("ids") Long[] ids, @Param("userId") Long userId);

    List<GoodsCategory> listCategoryForAdmin(@Param("categoryLevel") Integer categoryLevel,
                                             @Param("parentId") Long parentId);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds,
                                                           @Param("categoryLevel") int categoryLevel,
                                                           @Param("number") int number);
}