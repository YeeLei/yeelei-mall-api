package top.yeelei.mall.service;

import top.yeelei.mall.model.pojo.AdminUser;

public interface AdminManageUserService {

    /**
     * 管理员登录
     *
     * @param userName
     * @param passwordMd5
     * @return
     */
    String login(String userName, String passwordMd5);

    /**
     * 获取管理员用户信息
     *
     * @param adminUserId
     * @return
     */
    AdminUser getUserDetailById(Long adminUserId);

    /**
     * 修改当前用户登录的名称信息
     * @param adminUserId
     * @param loginUserName
     * @param nickName
     * @return
     */
    boolean updateName(Long adminUserId, String loginUserName, String nickName);

    /**
     * 修改当前用户登录的密码
     * @param adminUserId
     * @param newPassword
     * @param originalPassword
     * @return
     */
    boolean updatePassword(Long adminUserId, String newPassword, String originalPassword);

    /**
     * 注销用户
     * @param adminUserId
     */
    boolean logout(Long adminUserId);
}
