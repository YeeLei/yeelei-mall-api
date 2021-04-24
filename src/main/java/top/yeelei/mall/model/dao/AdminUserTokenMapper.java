package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.AdminUserToken;

@Repository
public interface AdminUserTokenMapper {
    int deleteByPrimaryKey(Long adminUserId);

    int insert(AdminUserToken record);

    int insertSelective(AdminUserToken record);

    AdminUserToken selectByPrimaryKey(Long adminUserId);

    int updateByPrimaryKeySelective(AdminUserToken record);

    int updateByPrimaryKey(AdminUserToken record);

    AdminUserToken selectByToken(String token);
}