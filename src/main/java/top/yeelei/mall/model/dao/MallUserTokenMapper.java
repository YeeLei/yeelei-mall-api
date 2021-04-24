package top.yeelei.mall.model.dao;

import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.MallUserToken;

@Repository
public interface MallUserTokenMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(MallUserToken record);

    int insertSelective(MallUserToken record);

    MallUserToken selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUserToken record);

    int updateByPrimaryKey(MallUserToken record);

    MallUserToken selectByToken(String token);
}