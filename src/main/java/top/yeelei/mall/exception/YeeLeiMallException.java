package top.yeelei.mall.exception;

public class YeeLeiMallException extends RuntimeException {

    public YeeLeiMallException() {
    }

    public YeeLeiMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new YeeLeiMallException(message);
    }

}
