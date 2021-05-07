package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.admin.param.AddCarouselParam;
import top.yeelei.mall.controller.admin.param.UpdateCarouselParam;
import top.yeelei.mall.controller.mall.vo.MallIndexCarouselVO;
import top.yeelei.mall.model.pojo.Carousel;

import java.util.List;

public interface AdminCarouselService {

    /**
     * 新增轮播图
     * @param addCarouselParam
     * @param adminUserId
     * @return
     */
    boolean addCarousel(AddCarouselParam addCarouselParam, Long adminUserId);

    /**
     * 获取轮播图信息
     * @param carouselId
     * @param adminUserId
     * @return
     */
    Carousel getCarouselById(Integer carouselId, Long adminUserId);

    /**
     * 根据ids批量删除轮播图
     * @param ids
     * @param adminUserId
     * @return
     */
    boolean deleteCarouselByIds(Long[] ids, Long adminUserId);

    /**
     * 更新轮播图
     * @param updateCarouselParam
     * @param adminUserId
     * @return
     */
    boolean updateCarousel(UpdateCarouselParam updateCarouselParam, Long adminUserId);

    /**
     * 获取轮播图列表（后台）
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo listCarouselForAdmin(Integer pageNum, Integer pageSize);

    /**
     * 前台首页轮播图列表
     * @param indexCarouselNumber
     * @return
     */
    List<MallIndexCarouselVO> getCarouselsForIndex(int indexCarouselNumber);
}
