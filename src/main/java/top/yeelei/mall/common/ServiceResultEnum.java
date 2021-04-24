package top.yeelei.mall.common;

public enum ServiceResultEnum {
    ERROR("error"),

    SUCCESS("success"),

    DATA_NOT_EXIST("未查询到记录！"),

    PARAM_ERROR("参数错误！"),

    SAME_CATEGORY_EXIST("有同级同名的分类！"),

    CATEGORY_ID_NOT_EXIST("分类id不存在!"),

    CATEGORY_ID_NOT_NULL("分类id不能为空!"),

    SAME_LOGIN_NAME_EXIST("用户名已存在！"),

    LOGIN_NAME_NULL("请输入登录名！"),

    LOGIN_NAME_IS_NOT_PHONE("请输入正确的手机号！"),

    LOGIN_PASSWORD_NULL("请输入密码！"),

    LOGIN_VERIFY_CODE_NULL("请输入验证码！"),

    LOGIN_VERIFY_CODE_ERROR("验证码错误！"),

    GOODS_NOT_EXIST("商品不存在！"),

    GOODS_PUT_DOWN("商品已下架！"),

    SELL_STATUS_ERROR("商品销售状态错误"),

    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("超出单个商品的最大购买数量！"),

    SHOPPING_CART_ITEM_NUMBER_ERROR("商品数量不能小于 1 ！"),

    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("超出购物车最大容量(20)！"),

    SHOPPING_CART_ITEM_EXIST_ERROR("已存在！无需重复添加！"),

    LOGIN_ERROR("用户名或密码错误！"),

    NOT_LOGIN_ERROR("未登录！"),

    ADMIN_PWD_NOT_ORIGNALPWD("原密码错误!"),

    ADMIN_NOT_LOGIN_ERROR("管理员未登录！"),

    TOKEN_EXPIRE_ERROR("无效认证！请重新登录！"),

    ADMIN_TOKEN_EXPIRE_ERROR("管理员登录过期！请重新登录！"),

    USER_NULL_ERROR("无效用户！请重新登录！"),

    LOGIN_USER_LOCKED_ERROR("用户已被禁止登录！"),

    DIRECTORY_FAILED("文件夹创建失败"),

    UPLOAD_FAILED("上传文件失败"),

    CAROUSEL_URL_NOT_NULL("轮播图url不能为空"),

    CAROUSEL_RANK_NOT_NULL("轮播图排序值不能为空"),

    ORDER_NOT_EXIST_ERROR("订单不存在！"),

    NULL_ADDRESS_ERROR("地址不能为空！"),

    ORDER_PRICE_ERROR("订单价格异常！"),

    ORDER_ITEM_NULL_ERROR("订单项异常！"),

    ORDER_GENERATE_ERROR("生成订单异常！"),

    SHOPPING_ITEM_ERROR("购物车数据异常！"),

    SHOPPING_CART_ITEM_NOT_NULL("购物项不能为空"),

    SHOPPING_CART_ITEM_IS_NULL("购物项为空"),

    SHOPPING_ITEM_COUNT_ERROR("库存不足！"),

    ORDER_STATUS_ERROR("订单状态异常！"),

    OPERATE_ERROR("操作失败！"),

    REQUEST_FORBIDEN_ERROR("禁止该操作！"),

    INDEX_CONFIG_ITEM_IS_EXIST("商品首页配置项已存在"),

    INDEX_CONFIG_ITEM_NOT_EXIST("商品首页配置项不存在"),

    DB_ERROR("database error"),

    SYSTEM_ERROR("系统错误!");

    private String result;

    ServiceResultEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
