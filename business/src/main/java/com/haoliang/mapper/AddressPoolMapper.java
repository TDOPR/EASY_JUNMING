
package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.AddressPool;

/**
 * @author Administrator
 */
public interface AddressPoolMapper extends BaseMapper<AddressPool> {

    String getAddress();

    void deleteByAddress(String address);
}
