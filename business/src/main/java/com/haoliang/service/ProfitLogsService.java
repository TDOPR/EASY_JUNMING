package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.vo.MyWalletsVO;

import java.util.List;


public interface ProfitLogsService extends IService<ProfitLogs> {

    /**
     * 修改收益信息表字段为已发放
     * @param idList
     * @return
     */
    boolean updateUseByIdList(List<Long> idList);

    /**
     * 查询量化收益明细
     * @param userId 用户Id
     * @return
     */
    MyWalletsVO.Quantification getMyQuantification(Integer userId);

}
