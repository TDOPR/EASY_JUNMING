package com.haoliang.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.constant.OperationAction;
import com.haoliang.common.constant.OperationModel;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.dto.IntIdListDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.SysFile;
import com.haoliang.model.condition.SysFileCondition;
import com.haoliang.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件管理
 * @author Dominick Li
 * @CreateTime 2022/11/22 16:39
 **/
@RequestMapping("/file")
@RestController
public class SysFileController {

    @Autowired
    private SysFileService sysFileService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:msg:list')")
    public JsonResult<PageVO<SysFile>> queryByCondition(@RequestBody PageParam<SysFile, SysFileCondition> pageParam) {
        if (pageParam.getSearchParam() == null) {
            pageParam.setSearchParam(new SysFileCondition());
        }
        IPage<SysFile> iPage = sysFileService.page(pageParam.getPage(), pageParam.getSearchParam().buildQueryParam());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    /**
     * 批量删除
     *
     * @param idList Id数组
     */
    @OperationLog(module = OperationModel.SYS_FILE, description = OperationAction.REMOVE)
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:file:remove')")
    public JsonResult deleteByIds(@RequestBody IntIdListDTO intIdListDTO) {
        return sysFileService.deleteByIds(intIdListDTO.getIdList());
    }

    /**
     * 新增文件
     */
    @OperationLog(module = OperationModel.SYS_FILE, description = OperationAction.ADD_OR_EDIT)
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('sys:file:add')")
    public JsonResult add(@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token,@RequestParam("file") MultipartFile file,@RequestParam String fileName,@RequestParam String fileType,@RequestParam String fileDesc) {
        return sysFileService.saveAndUpload(token,file, fileName, fileType, fileDesc);
    }

    /**
     * 修改文件信息
     * @param id 文件Id
     * @param fileName 文件名称
     * @param fileDesc 文件描述
     * @return
     */
    @OperationLog(module = OperationModel.SYS_FILE, description = OperationAction.ADD_OR_EDIT)
    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('sys:file:edit')")
    public JsonResult edit(Integer id, String fileName, String fileDesc) {
        return sysFileService.updateFileInfo(id, fileName, fileDesc);
    }

    /**
     * 下载文件
     * @param id 文件Id
     */
    @PreAuthorize("hasAuthority('sys:file:download')")
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Integer id, HttpServletResponse httpServletResponse) throws Exception {
        sysFileService.downloadFile(id, httpServletResponse);
    }

}
