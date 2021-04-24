package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.Carousel;

import java.util.List;

@Repository
public interface CarouselMapper {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel record);

    int insertSelective(Carousel record);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimaryKey(Carousel record);

    void deleteByIds(@Param("ids") Long[] ids, @Param("userId") Long userId);

    List<Carousel> selectList();

    List<Carousel> selectCarouselByNum(@Param("number") int number);
}