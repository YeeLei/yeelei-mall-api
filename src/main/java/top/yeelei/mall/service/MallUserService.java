package top.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import top.yeelei.mall.controller.mall.param.MallUserUpdateParam;

public interface MallUserService {
    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * 用户登录
     *
     * @param loginName
     * @param passwordMd5
     * @return
     */
    String login(String loginName, String passwordMd5);

    /**
     * 用户注销
     *
     * @param userId
     * @return
     */
    boolean logout(Long userId);

    /**
     * 用户信息修改
     * @param mallUser
     * @param userId
     * @return
     */
    Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId);

    /**
     * 后台用户列表
     * @param pageNum
     * @param pageSize
     * @param lockStatus
     * @return
     */
    PageInfo listForMallUser(Integer pageNum, Integer pageSize,Integer lockStatus);

    /**
     * 批量给用户上锁
     * @param ids
     * @param lockStatus
     * @return
     */
    boolean lockUsers(Long[] ids, Integer lockStatus);
}
