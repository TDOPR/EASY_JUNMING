package com.haoliang.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.AppUsers;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.dto.AppUserDTO;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.dto.AppUserRegisterDTO;
import com.haoliang.model.dto.FindPasswordDTO;
import com.haoliang.model.vo.AppTokenVO;
import com.haoliang.model.vo.AppUsersVO;
import com.haoliang.model.vo.BusinessVO;
import com.haoliang.model.vo.MyDetailVO;
import org.springframework.web.multipart.MultipartFile;

public interface AppUserService extends IService<AppUsers> {

    JsonResult<AppTokenVO> login(AppUserLoginDTO appUserLoginDTO, String localIp);

    JsonResult register(AppUserRegisterDTO appUserRegisterDTO);

    JsonResult findPassword(FindPasswordDTO findPasswordDTO);

    JsonResult home();

    JsonResult<PageVO<AppUsersVO>> pageList(PageParam<AppUsers, AppUsersCondition> pageParam);

    JsonResult modifyUserDetail(AppUserDTO appUserDTO);

    JsonResult uploadHeadImage(MultipartFile file) throws Exception;

    JsonResult updatePassword(UpdatePasswordDTO updatePasswordDTO);

    BusinessVO getBusinessVO();

    JsonResult<MyDetailVO> getMyDetail();

    AppUsers selectColumnsByUserId(Integer userId, SFunction<AppUsers, ?>... columns);
}
