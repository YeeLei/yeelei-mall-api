package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.AdminUser;

@Repository
public interface AdminUserMapper {
    int deleteByPrimaryKey(Long adminUserId);

    int insert(AdminUser record);

    int insertSelective(AdminUser record);

    AdminUser selectByPrimaryKey(Long adminUserId);

    int updateByPrimaryKeySelective(AdminUser record);

    int updateByPrimaryKey(AdminUser record);

    AdminUser selectByLoginNameAndPassword(String userName, String passwordMd5);
}