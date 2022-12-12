package com.haoliang.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.AppParamProperties;
import com.haoliang.common.config.GlobalProperties;
import com.haoliang.common.enums.ContentTypeEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.*;
import com.haoliang.mapper.SysFileMapper;
import com.haoliang.model.SysFile;
import com.haoliang.service.SysFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/22 16:42
 **/
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    @Resource
    private AppParamProperties AppParamProperties;

    @Override
    public void downloadFile(Integer id, HttpServletResponse httpServletResponse) throws Exception {
        SysFile sysFile = this.getById(id);
        ResponseUtil.downloadFileByLocal(httpServletResponse, new File(sysFile.getFilePath()), ContentTypeEnum.OCTET_STREAM);
    }

    @Override
    public JsonResult deleteByIds(List<Integer> idList) {
        List<SysFile> sysFileList = this.list(new LambdaQueryWrapper<SysFile>().select(SysFile::getFilePath).in(SysFile::getId, idList));
        for (SysFile sysFile : sysFileList) {
            FileUtil.del(sysFile.getFilePath());
        }
        this.removeByIds(idList);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult saveAndUpload( MultipartFile file, String fileName, String fileType, String fileDesc) {
        try {
            String userName = JwtTokenUtil.getUserNameFromToken(ThreadLocalManager.getToken());
            SysFile sysFile = new SysFile();
            sysFile.setFileType(fileType);
            sysFile.setFileDesc(fileDesc);
            sysFile.setUsername(userName);
            sysFile.setFileName(fileName);


            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            String saveFileName = IdUtil.simpleUUID() + "." + suffix;
            String savePath = AppParamProperties.getSysfileSavePath();
            String url = GlobalProperties.getVirtualPathURL() + StringUtil.replace(AppParamProperties.getSysfileSavePath(), AppParamProperties.getRootPath(), "") + saveFileName;
            //复制文件流到本地文件
            org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), new File(savePath, saveFileName));
            sysFile.setFilePath(savePath+saveFileName);
            sysFile.setDownloadPath(url);
            this.save(sysFile);
            return JsonResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorLogUtil.save(e);
            return JsonResult.failureResult();
        }
    }

    @Override
    public JsonResult updateFileInfo(Integer id, String fileName, String fileType) {
        UpdateWrapper<SysFile> sysFileUpdateWrapper = Wrappers.update();
        sysFileUpdateWrapper.lambda()
                .set(SysFile::getFileName, fileName)
                .eq(SysFile::getId, id);
        this.update(sysFileUpdateWrapper);
        return JsonResult.successResult();
    }
}
