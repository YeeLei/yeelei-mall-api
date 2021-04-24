package top.yeelei.mall.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 响应结果生成
 */
@Data
public class ApiRestResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    //业务码，比如成功、失败、权限不足等 code，可自行定义
    @ApiModelProperty("返回码")
    private int resultCode;

    //返回信息，后端在进行业务处理后返回给前端一个提示信息，可自行定义
    @ApiModelProperty("返回信息")
    private String message;

    //数据结果，泛型，可以是列表、单个对象、数字、布尔值等
    @ApiModelProperty("返回数据")
    private T data;

    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    public static ApiRestResponse genSuccessResult() {
        ApiRestResponse apiRestResponse = new ApiRestResponse<>();
        apiRestResponse.setResultCode(RESULT_CODE_SUCCESS);
        apiRestResponse.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return apiRestResponse;
    }

    public static ApiRestResponse genSuccessResult(String message) {
        ApiRestResponse apiRestResponse = new ApiRestResponse<>();
        apiRestResponse.setResultCode(RESULT_CODE_SUCCESS);
        apiRestResponse.setMessage(message);
        return apiRestResponse;
    }

    public static ApiRestResponse genSuccessResult(Object data) {
        ApiRestResponse apiRestResponse = new ApiRestResponse<>();
        apiRestResponse.setResultCode(RESULT_CODE_SUCCESS);
        apiRestResponse.setMessage(DEFAULT_SUCCESS_MESSAGE);
        apiRestResponse.setData(data);
        return apiRestResponse;
    }

    public static ApiRestResponse genFailResult(String message) {
        ApiRestResponse apiRestResponse = new ApiRestResponse<>();
        apiRestResponse.setResultCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)) {
            apiRestResponse.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            apiRestResponse.setMessage(message);
        }
        return apiRestResponse;
    }

    public static ApiRestResponse genErrorResult(int code, String message) {
        ApiRestResponse apiRestResponse = new ApiRestResponse<>();
        apiRestResponse.setResultCode(code);
        apiRestResponse.setMessage(message);
        return apiRestResponse;
    }
}
