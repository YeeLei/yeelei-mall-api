package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.yeelei.mall.controller.admin.param.AddCarouselParam;
import top.yeelei.mall.controller.admin.param.UpdateCarouselParam;
import top.yeelei.mall.controller.mall.vo.MallIndexCarouselVO;
import top.yeelei.mall.model.dao.AdminUserMapper;
import top.yeelei.mall.model.dao.CarouselMapper;
import top.yeelei.mall.model.pojo.AdminUser;
import top.yeelei.mall.model.pojo.Carousel;
import top.yeelei.mall.service.AdminCarouselService;
import top.yeelei.mall.utils.CopyListUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class AdminCarouselServiceImpl implements AdminCarouselService {
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public boolean addCarousel(AddCarouselParam addCarouselParam, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            Carousel carousel = new Carousel();
            carousel.setCarouselUrl(addCarouselParam.getCarouselUrl());
            carousel.setRedirectUrl(addCarouselParam.getRedirectUrl());
            carousel.setCarouselRank(addCarouselParam.getCarouselRank());
            long userId = adminUser.getAdminUserId();
            carousel.setCreateUser((int) userId);
            //新增信息
            if (carouselMapper.insertSelective(carousel) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateCarousel(UpdateCarouselParam updateCarouselParam, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            Carousel carousel = new Carousel();
            carousel.setCarouselId(updateCarouselParam.getCarouselId());
            carousel.setCarouselUrl(updateCarouselParam.getCarouselUrl());
            carousel.setRedirectUrl(updateCarouselParam.getRedirectUrl());
            carousel.setCarouselRank(updateCarouselParam.getCarouselRank());
            long userId = adminUser.getAdminUserId();
            carousel.setUpdateUser((int) userId);
            carousel.setUpdateTime(new Date());
            //更新信息
            if (carouselMapper.updateByPrimaryKeySelective(carousel) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Carousel getCarouselById(Integer carouselId, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            Carousel carousel = carouselMapper.selectByPrimaryKey(carouselId);
            return carousel;
        }
        return null;
    }

    @Override
    public boolean deleteCarouselByIds(Long[] ids, Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            carouselMapper.deleteByIds(ids, adminUserId);
            return true;
        }
        return false;
    }

    @Override
    public PageInfo listCarouselForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<Carousel> carouselList = carouselMapper.selectList();
        PageInfo<Carousel> pageInfo = new PageInfo<>(carouselList);
        return pageInfo;
    }

    @Override
    public List<MallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<MallIndexCarouselVO> mallIndexCarouselVOS = new ArrayList<>(number);
        List<Carousel> carousels = carouselMapper.selectCarouselByNum(number);
        if (!CollectionUtils.isEmpty(carousels)) {
            //BeanUtils.copyProperties(carousels, mallIndexCarouselVOS); // 赋值失败
            mallIndexCarouselVOS = CopyListUtil.copyListProperties(carousels, MallIndexCarouselVO::new);
        }

        return mallIndexCarouselVOS;
    }

}
