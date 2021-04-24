package top.yeelei.mall.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import top.yeelei.mall.common.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 */
public class MD5Util {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue + Constants.SALT).getBytes()));
    }

    //测试生成的md5的值
    public static void main(String[] args) {
        String md5Str = null;
        try {
            md5Str = getMD5Str("12345678");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(md5Str);
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}
