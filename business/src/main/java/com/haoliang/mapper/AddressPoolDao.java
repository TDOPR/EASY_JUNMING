
package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.AddressPoolEntity;
import com.haoliang.model.EvmEventEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public interface AddressPoolDao extends BaseMapper<AddressPoolEntity> {
    String getAddress();
    void deleteByAddress(String address);
}
