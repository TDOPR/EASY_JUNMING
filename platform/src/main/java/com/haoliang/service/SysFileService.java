package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SysFileService extends IService<SysFile> {

    void downloadFile(Integer id, HttpServletResponse httpServletResponse) throws Exception;

    JsonResult deleteByIds(List<Integer> idList);

    JsonResult saveAndUpload(String token,MultipartFile file, String fileName, String fileType, String fileDesc);

    JsonResult updateFileInfo(Integer id, String fileName, String fileType);
}
