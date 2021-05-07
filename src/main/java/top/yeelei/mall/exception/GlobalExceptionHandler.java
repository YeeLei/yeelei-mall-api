package top.yeelei.mall.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.ServiceResultEnum;

import java.util.ArrayList;
import java.util.List;


/**
 * 描述：处理统一异常的handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
        logger.error("Default Exception: ", e);
        return ApiRestResponse.genFailResult(ServiceResultEnum.SYSTEM_ERROR.getResult());
    }

    @ExceptionHandler(YeeLeiMallException.class)
    @ResponseBody
    public Object handleYeeLeiMallException(YeeLeiMallException e) {
        logger.error("YeeLeiMallException: ", e);
        return ApiRestResponse.genFailResult(e.getMessage());
    }

    @ExceptionHandler(MallAdminException.class)
    @ResponseBody
    public Object handleMallAdminException(MallAdminException e) {
        logger.error("MallAdminException: ", e);
        return ApiRestResponse.genErrorResult(e.getCode(),e.getMessage());
    }
    //拦截由@valid报出的异常MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("MethodArgumentNotValidException: ", e);
        return handleBindingResult(e.getBindingResult());
    }
    private ApiRestResponse handleBindingResult(BindingResult result) {
        //把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError objectError : allErrors) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            return ApiRestResponse.genFailResult("请求参数错误");
        }
        return ApiRestResponse.genFailResult(list.toString());
    }
}
