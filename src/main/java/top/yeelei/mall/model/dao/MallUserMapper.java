package top.yeelei.mall.model.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.yeelei.mall.model.pojo.MallUser;

import java.util.List;

@Repository
public interface MallUserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(MallUser record);

    int insertSelective(MallUser record);

    MallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUser record);

    int updateByPrimaryKey(MallUser record);

    MallUser selectByLoginName(String loginName);

    MallUser selectByLoginNameAndPassword(@Param("loginName") String loginName,
                                          @Param("password") String password);

    List<MallUser> selectMallUserList(Integer lockStatus);

    int updateMallUserLockByIds(@Param("ids") Long[] ids,
                                @Param("lockStatus") Integer lockStatus);
}