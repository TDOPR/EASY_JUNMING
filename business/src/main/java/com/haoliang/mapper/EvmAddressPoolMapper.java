
package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.EvmAddressPool;

/**
 * @author Administrator
 */
public interface EvmAddressPoolMapper extends BaseMapper<EvmAddressPool> {

    EvmAddressPool randomGetAddress(String coinType);

    void deleteByAddress(String address);
}
