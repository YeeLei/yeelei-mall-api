package top.yeelei.mall.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import top.yeelei.mall.common.ApiRestResponse;
import top.yeelei.mall.common.Constants;
import top.yeelei.mall.common.ServiceResultEnum;
import top.yeelei.mall.config.annotation.TokenToAdminUser;
import top.yeelei.mall.model.pojo.AdminUserToken;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController
@Api(tags = "后台管理系统文件上传接口")
@RequestMapping("/manage-api")
public class AdminUploadApi {
    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    /**
     * 图片上传
     */
    @PostMapping("/upload/file")
    @ApiOperation(value = "单图上传", notes = "file Name \"file\"")
    public ApiRestResponse upload(@RequestParam("file") MultipartFile file,
                                  @TokenToAdminUser AdminUserToken adminUser,
                                  HttpServletRequest httpServletRequest) {
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid + suffixName;
        //创建文件
        File directoryName = new File(Constants.FILE_UPLOAD_DIC);
        File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
        if (!directoryName.exists()) {
            if (!directoryName.mkdir()) {
                return ApiRestResponse.genFailResult(ServiceResultEnum.DIRECTORY_FAILED.getResult());
            }
        }
        try {
            file.transferTo(destFile);
            try {
                ApiRestResponse apiRestResponse = ApiRestResponse.genSuccessResult();
                apiRestResponse.setData(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/upload/" + newFileName);
                return apiRestResponse;
            } catch (URISyntaxException e) {
                return ApiRestResponse.genFailResult(ServiceResultEnum.UPLOAD_FAILED.getResult());
            }
        } catch (IOException e) {
            return ApiRestResponse.genFailResult(ServiceResultEnum.UPLOAD_FAILED.getResult());
        }
    }

    /**
     * 多张图片上传
     */
    @PostMapping("/upload/files")
    @ApiOperation(value = "多图上传", notes = "wangEditor图片上传")
    public ApiRestResponse wangEditorUpload(HttpServletRequest httpServletRequest, @TokenToAdminUser AdminUserToken adminUser) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
            Iterator<String> itr = multipartRequest.getFileNames();
            System.out.println(multipartRequest.getFileNames());
            int total = 0;
            while (itr.hasNext()) {
                if (total > 5) {
                    return ApiRestResponse.genFailResult("最多上传5张图片");
                }
                total++;
                MultipartFile file = multipartRequest.getFile(itr.next());
                multipartFiles.add(file);
            }
        }
        if (CollectionUtils.isEmpty(multipartFiles)) {
            return ApiRestResponse.genFailResult("上传参数异常");
        }
        if (multipartFiles != null && multipartFiles.size() > 5) {
            return ApiRestResponse.genFailResult("最多上传5张图片");
        }
        List<String> fileNames = new ArrayList<>(multipartFiles.size());
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取文件后缀名
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
            UUID uuid = UUID.randomUUID();
            String newFileName = uuid + suffixName;
            //创建文件
            File directoryName = new File(Constants.FILE_UPLOAD_DIC);
            File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
            if (!directoryName.exists()) {
                if (!directoryName.mkdir()) {
                    return ApiRestResponse.genFailResult(ServiceResultEnum.DIRECTORY_FAILED.getResult());
                }
            }
            try {
                multipartFile.transferTo(destFile);
                try {
                    fileNames.add(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/upload/" + newFileName);
                } catch (URISyntaxException e) {
                    return ApiRestResponse.genFailResult(ServiceResultEnum.UPLOAD_FAILED.getResult());
                }
            } catch (IOException e) {
                return ApiRestResponse.genFailResult(ServiceResultEnum.UPLOAD_FAILED.getResult());
            }
        }
        ApiRestResponse apiRestResponse = ApiRestResponse.genSuccessResult();
        apiRestResponse.setData(fileNames);
        return apiRestResponse;
    }

    /**
     * 获取主机地址
     *
     * @param uri
     * @return
     */
    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }
}
