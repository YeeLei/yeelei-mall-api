package top.yeelei.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.model.dao.AdminUserMapper;
import top.yeelei.mall.model.dao.AdminUserTokenMapper;
import top.yeelei.mall.model.pojo.AdminUser;
import top.yeelei.mall.model.pojo.AdminUserToken;
import top.yeelei.mall.service.AdminManageUserService;
import top.yeelei.mall.utils.MD5Util;
import top.yeelei.mall.utils.NumberUtil;
import top.yeelei.mall.utils.SystemUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class AdminManageUserServiceImpl implements AdminManageUserService {
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private AdminUserTokenMapper adminUserTokenMapper;

    @Override
    public String login(String userName, String passwordMd5) {
        //先得到md5加密后的密码
        String md5 = null;
        try {
            md5 = MD5Util.getMD5Str(passwordMd5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //根据用户名和加密后的密码查询数据库
        AdminUser adminUser = adminUserMapper.selectByLoginNameAndPassword(userName, md5);
        if (adminUser != null) {
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", adminUser.getAdminUserId());
            AdminUserToken adminUserToken = adminUserTokenMapper.selectByPrimaryKey(adminUser.getAdminUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);//过期时间 48 小时
            if (adminUserToken == null) {
                adminUserToken = new AdminUserToken();
                adminUserToken.setAdminUserId(adminUser.getAdminUserId());
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (adminUserTokenMapper.insertSelective(adminUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                adminUserToken.setToken(token);
                adminUserToken.setUpdateTime(now);
                adminUserToken.setExpireTime(expireTime);
                //更新
                if (adminUserTokenMapper.updateByPrimaryKeySelective(adminUserToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public AdminUser getUserDetailById(Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser == null) {
            YeeLeiMallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
        }
        return adminUser;
    }

    @Override
    public boolean updateName(Long adminUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser == null) {
            YeeLeiMallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
        }
        //设置新名称并修改
        adminUser.setLoginUserName(loginUserName);
        adminUser.setNickName(nickName);
        int count = adminUserMapper.updateByPrimaryKeySelective(adminUser);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePassword(Long adminUserId, String newPassword, String originalPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(adminUserId);
        if (adminUser != null) {
            //比较原密码是否正确
            try {
                if (MD5Util.getMD5Str(originalPassword).equals(adminUser.getLoginPassword())) {
                    //设置新密码并修改
                    adminUser.setLoginPassword(MD5Util.getMD5Str(newPassword));
                    //如果更新密码成功并且删除当前当前登录用户token成功
                    if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0 &&
                            adminUserTokenMapper.deleteByPrimaryKey(adminUser.getAdminUserId()) > 0) {
                        return true;
                    }
                }else {
                    YeeLeiMallException.fail(ServiceResultEnum.ADMIN_PWD_NOT_ORIGNALPWD.getResult());
                }
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean logout(Long adminUserId) {
        return adminUserTokenMapper.deleteByPrimaryKey(adminUserId) >0;
    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }
}
