package top.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.exception.YeeLeiMallException;
import top.yeelei.mall.controller.MallController.param.MallUserUpdateParam;
import top.yeelei.mall.model.dao.MallUserMapper;
import top.yeelei.mall.model.dao.MallUserTokenMapper;
import top.yeelei.mall.model.pojo.MallUser;
import top.yeelei.mall.model.pojo.MallUserToken;
import top.yeelei.mall.service.MallUserService;
import top.yeelei.mall.utils.MD5Util;
import top.yeelei.mall.utils.NumberUtil;
import top.yeelei.mall.utils.SystemUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@Service
public class MallUserServiceImpl implements MallUserService {
    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private MallUserTokenMapper mallUserTokenMapper;

    @Override
    public String register(String loginName, String password) {
        //判断用户名存在
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        String passwordMD5 = null;
        try {
            passwordMD5 = MD5Util.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public String login(String loginName, String password) {
        //先得到md5加密后的密码
        String md5 = null;
        try {
            md5 = MD5Util.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //根据用户名和加密后的密码查询数据库
        MallUser user = mallUserMapper.selectByLoginNameAndPassword(loginName, md5);
        if (user != null) {
            //判断用户是否被禁止登陆
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            MallUserToken mallUserToken = mallUserTokenMapper.selectByPrimaryKey(user.getUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);//过期时间 48 小时
            if (mallUserToken == null) {
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (mallUserTokenMapper.insertSelective(mallUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //更新
                if (mallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public boolean logout(Long userId) {
        return mallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            YeeLeiMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
        try {
            if (MD5Util.getMD5Str("").equals(mallUser.getPasswordMd5())) {
                user.setPasswordMd5(user.getPasswordMd5());
            } else {
                user.setPasswordMd5(MD5Util.getMD5Str(mallUser.getPasswordMd5()));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        user.setIntroduceSign(mallUser.getIntroduceSign());
        //更新信息
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo listForMallUser(Integer pageNum, Integer pageSize, Integer lockStatus) {
        PageHelper.startPage(pageNum, pageSize);
        List<MallUser> mallUserList = mallUserMapper.selectMallUserList(lockStatus);
        PageInfo<MallUser> pageInfo = new PageInfo<>(mallUserList);
        return pageInfo;
    }

    @Override
    public boolean lockUsers(Long[] ids, Integer lockStatus) {
        return mallUserMapper.updateMallUserLockByIds(ids, lockStatus) > 0;
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
