package top.yeelei.mall.exception;

public class MallAdminException extends RuntimeException {

    private final Integer resultCode;
    private final String message;

    public MallAdminException(Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public Integer getCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
